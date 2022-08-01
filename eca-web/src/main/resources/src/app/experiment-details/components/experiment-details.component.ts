import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  ExperimentDto,
  ExperimentErsReportDto,
  ExperimentProgressDto, PushRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { ActivatedRoute } from "@angular/router";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { ExperimentFields } from "../../common/util/field-names";
import { FieldLink } from "../../common/model/field-link";
import { FieldService } from "../../common/services/field.service";
import { Utils } from "../../common/util/utils";
import { Subscription, timer } from "rxjs";
import { RequestStatus } from "../../common/model/request-status.enum";
import { WsService } from "../../common/websockets/ws.service";
import { environment } from "../../../environments/environment";
import { Logger } from "../../common/util/logging";
import { filter } from "rxjs/internal/operators";
import { PushVariables } from "../../common/util/push-variables";

@Component({
  selector: 'app-experiment-details',
  templateUrl: './experiment-details.component.html',
  styleUrls: ['./experiment-details.component.scss']
})
export class ExperimentDetailsComponent implements OnInit, OnDestroy, FieldLink {

  private readonly id: number;

  private readonly updateProgressInterval = 1000;

  private readonly finalRequestStatuses = ['FINISHED', 'ERROR', 'TIMEOUT'];

  public experimentFields: any[] = [];

  public experimentDto: ExperimentDto;

  public experimentErsReport: ExperimentErsReportDto;

  public experimentProgress: ExperimentProgressDto;

  public loading = false;

  public linkColumns: string[] = [ExperimentFields.EXPERIMENT_PATH];

  private wsService: WsService;

  private experimentProgressSubscription: Subscription;
  private experimentUpdatesSubscription: Subscription;

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService,
                     private route: ActivatedRoute,
                     private fieldService: FieldService) {
    this.id = this.route.snapshot.params.id;
    this.initExperimentFields();
  }

  public ngOnInit(): void {
    this.getExperiment();
    this.getExperimentErsReport();
  }

  public ngOnDestroy(): void {
    this.unSubscribeExperimentUpdates();
    this.unSubscribeExperimentProgress();
  }

  public getExperiment(): void {
    this.experimentsService.getExperiment(this.id)
      .subscribe({
        next: (experimentDto: ExperimentDto) => {
          this.experimentDto = experimentDto;
          if (!this.finalRequestStatuses.includes(this.experimentDto.requestStatus.value)) {
            //Subscribe for experiment status change
            this.subscribeForExperimentUpdate();
            if (this.experimentDto.requestStatus.value == RequestStatus.IN_PROGRESS) {
              //Subscribe for experiment progress change
              this.subscribeForExperimentProgressUpdate();
            }
          }
        },
        error: (error) => {
          this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
        }
      });
  }

  public getExperimentResultsFile(): void {
    this.loading = true;
    this.experimentsService.downloadExperimentResults(this.experimentDto,
      () => this.loading = false,
      (error) => this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message })
    );
  }

  public getExperimentErsReport(): void {
    this.experimentsService.getExperimentErsReport(this.id)
      .subscribe({
        next: (experimentErsReport: ExperimentErsReportDto) => {
          this.experimentErsReport = experimentErsReport;
        },
        error: (error) => {
          this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
        }
      });
  }

  public isLink(field: string): boolean {
    return this.linkColumns.includes(field) && this.hasValue(field);
  }

  public showProgressBar(field: string): boolean {
    return field == ExperimentFields.EXPERIMENT_PATH && this.loading;
  }

  public onLink(field: string) {
    if (field === ExperimentFields.EXPERIMENT_PATH) {
      this.getExperimentResultsFile();
    } else {
      this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${field} as link`});
    }
  }

  public getExperimentValue(field: string) {
    if (this.experimentDto) {
      if (field == ExperimentFields.EVALUATION_METHOD_DESCRIPTION) {
        return Utils.getEvaluationMethodDescription(this.experimentDto.evaluationMethod,
          this.experimentDto.numFolds, this.experimentDto.numTests);
      } else {
        return this.fieldService.getFieldValue(field, this.experimentDto, Utils.MISSING_VALUE);
      }
    }
    return null;
  }

  public hasValue(field: string): boolean {
    return this.fieldService.hasValue(field, this.experimentDto);
  }

  private subscribeForExperimentProgressUpdate(): void {
    if (!this.experimentProgressSubscription) {
      Logger.debug(`Subscribe experiment ${this.experimentDto.requestId} progress change`);
      this.experimentProgressSubscription = timer(0, this.updateProgressInterval).subscribe({
        next: () => {
          this.experimentsService.getExperimentProgress(this.id)
            .subscribe({
              next: (experimentProgress: ExperimentProgressDto) => {
                this.experimentProgress = experimentProgress;
                if (experimentProgress.finished) {
                  this.unSubscribeExperimentProgress();
                }
              },
              error: (error) => {
                this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
              }
            });
        },
        error: (error) => {
          this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
        }
      });
    }
  }

  private subscribeForExperimentUpdate(): void {
    if (!this.experimentUpdatesSubscription) {
      Logger.debug(`Subscribe experiment ${this.experimentDto.requestId} status change`);
      this.wsService = new WsService();
      this.experimentUpdatesSubscription = this.wsService.subscribe(environment.experimentsQueue)
        .pipe(
          filter(message => {
            const pushRequestDto: PushRequestDto = JSON.parse(message.body);
            const id = pushRequestDto.additionalProperties[PushVariables.EXPERIMENT_ID];
            return this.experimentDto.id == Number(id);
          })
        )
        .subscribe({
          next: (message) => {
            Logger.debug(`Received experiment web push ${message.body}`);
            const pushRequestDto: PushRequestDto = JSON.parse(message.body);
            this.handleExperimentPush(pushRequestDto);
            this.getExperiment();
            this.getExperimentErsReport();
          },
          error: (error) => {
            this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
          }
        });
    }
  }

  private handleExperimentPush(pushRequestDto: PushRequestDto): void {
    const requestStatus = pushRequestDto.additionalProperties[PushVariables.EXPERIMENT_REQUEST_STATUS];
    if (this.finalRequestStatuses.includes(requestStatus)) {
      this.unSubscribeExperimentUpdates();
    }
  }

  private unSubscribeExperimentUpdates(): void {
    if (this.experimentUpdatesSubscription) {
      this.experimentUpdatesSubscription.unsubscribe();
      this.experimentUpdatesSubscription = null;
      Logger.debug(`Unsubscribe experiment ${this.experimentDto.requestId} status change`);
    }
    if (this.wsService) {
      this.wsService.close();
      this.wsService = null;
    }
  }

  private unSubscribeExperimentProgress(): void {
    if (this.experimentProgressSubscription) {
      this.experimentProgressSubscription.unsubscribe();
      this.experimentProgressSubscription = null;
      Logger.debug(`Unsubscribe experiment ${this.experimentDto.requestId} progress change`);
    }
  }

  private initExperimentFields(): void {
    this.experimentFields = [
      { name: ExperimentFields.REQUEST_ID, label: "UUID заявки" },
      { name: ExperimentFields.REQUEST_STATUS_DESCRIPTION, label: "Статус заявки" },
      { name: ExperimentFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности" },
      { name: ExperimentFields.EMAIL, label: "Email заявки" },
      { name: ExperimentFields.RELATION_NAME, label: "Обучающая выборка:" },
      { name: ExperimentFields.NUM_INSTANCES, label: "Число объектов:" },
      { name: ExperimentFields.NUM_ATTRIBUTES, label: "Число атрибутов:" },
      { name: ExperimentFields.NUM_CLASSES, label: "Число классов:" },
      { name: ExperimentFields.CLASS_NAME, label: "Атрибут класса:" },
      { name: ExperimentFields.EVALUATION_TOTAL_TIME, label: "Время построения эксперимента" },
      { name: ExperimentFields.CREATION_DATE, label: "Дата создания заявки" },
      { name: ExperimentFields.START_DATE, label: "Дата начала эксперимента" },
      { name: ExperimentFields.END_DATE, label: "Дата окончания эксперимента" },
      { name: ExperimentFields.DELETED_DATE, label: "Дата удаления результатов" },
      { name: ExperimentFields.EXPERIMENT_PATH, label: "Результаты эксперимента" }
    ];
  }
}

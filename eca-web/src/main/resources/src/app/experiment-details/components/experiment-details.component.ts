import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  ExperimentDto,
  ExperimentErsReportDto,
  ExperimentProgressDto, PushRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { ActivatedRoute, NavigationEnd, Router, RouterEvent } from "@angular/router";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { ExperimentFields } from "../../common/util/field-names";
import { FieldLink } from "../../common/model/field-link";
import { FieldService } from "../../common/services/field.service";
import { Utils } from "../../common/util/utils";
import { Subscription, timer } from "rxjs";
import { RequestStatus } from "../../common/model/request-status.enum";
import { Logger } from "../../common/util/logging";
import { PushVariables } from "../../common/util/push-variables";
import { PushService } from "../../common/push/push.service";
import { PushMessageType } from "../../common/util/push-message.type";
import { filter, finalize } from 'rxjs/internal/operators';

@Component({
  selector: 'app-experiment-details',
  templateUrl: './experiment-details.component.html',
  styleUrls: ['./experiment-details.component.scss']
})
export class ExperimentDetailsComponent implements OnInit, OnDestroy, FieldLink {

  private id: number;

  private readonly updateProgressInterval = 1000;

  private readonly finalRequestStatuses = ['FINISHED', 'ERROR', 'TIMEOUT', 'CANCELED'];

  public experimentFields: any[] = [];

  public experimentDto: ExperimentDto;

  public experimentErsReport: ExperimentErsReportDto;

  public experimentProgress: ExperimentProgressDto;

  public loading = false;

  public linkColumns: string[] = [ExperimentFields.MODEL_PATH];

  public blink = false;

  private experimentProgressSubscription: Subscription;
  private experimentUpdatesSubscription: Subscription;
  private routeUpdateSubscription: Subscription;
  private experimentProgressTimerSubscription: Subscription;

  private currentDate: Date = new Date();

  private readonly timerInterval = 1000;

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService,
                     private route: ActivatedRoute,
                     private pushService: PushService,
                     private router: Router,
                     private fieldService: FieldService) {
    this.id = this.route.snapshot.params.id;
    this.initExperimentFields();
  }

  public ngOnInit(): void {
    this.getExperimentFullData();
    this.subscribeForRouteChanges();
  }

  public ngOnDestroy(): void {
    this.unSubscribeExperimentUpdates();
    this.unSubscribeExperimentProgress();
    this.stopExperimentProgressTimer();
    this.routeUpdateSubscription.unsubscribe();
  }

  public getExperimentFullData(): void {
    this.blink = false;
    this.getExperiment();
    this.getExperimentErsReport();
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
              this.startExperimentProgressTimer();
            }
          }
        },
        error: (error) => {
          this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
        }
      });
  }

  public cancelExperiment(): void {
    this.experimentsService.cancelExperiment(this.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.unSubscribeExperimentProgress();
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
    return field == ExperimentFields.MODEL_PATH && this.loading;
  }

  public onLink(field: string) {
    if (field === ExperimentFields.MODEL_PATH) {
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

  public isRequestStatusBlink(field: string, requestStatus: string): boolean {
    return this.blink && field == 'requestStatus.description' && this.experimentDto && this.experimentDto.requestStatus.value == requestStatus;
  }

  public getFormattedExperimentProgressTime(): string {
    if (this.experimentDto && this.experimentDto.startDate) {
      const startMillis = new Date(this.experimentDto.startDate).getTime();
      const currentMillis = this.currentDate.getTime();
      return Utils.getFormattedTimeString(currentMillis - startMillis);
    }
    return null;
  }

  private subscribeForRouteChanges(): void {
    //Subscribe for route changes in current details component
    //Used for route from experiment details to another experiment details via push
    this.routeUpdateSubscription = this.router.events.pipe(
      filter((event: RouterEvent) => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.unSubscribeExperimentProgress();
      this.unSubscribeExperimentUpdates();
      this.stopExperimentProgressTimer();
      this.id = this.route.snapshot.params.id;
      this.getExperimentFullData();
    });
  }

  private subscribeForExperimentProgressUpdate(): void {
    if (!this.experimentProgressSubscription) {
      this.experimentProgress = null;
      Logger.debug(`Subscribe experiment ${this.experimentDto.requestId} progress change`);
      this.experimentProgressSubscription = timer(0, this.updateProgressInterval).subscribe({
        next: () => {
          this.experimentsService.getExperimentProgress(this.id)
            .subscribe({
              next: (experimentProgress: ExperimentProgressDto) => {
                this.experimentProgress = experimentProgress;
                if (experimentProgress.finished) {
                  this.unSubscribeExperimentProgress();
                  this.stopExperimentProgressTimer();
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
      const filterPredicate = (pushRequestDto: PushRequestDto) => {
        if (pushRequestDto.pushType == 'SYSTEM' && pushRequestDto.messageType == PushMessageType.EXPERIMENT_STATUS_CHANGE) {
          const id = pushRequestDto.additionalProperties[PushVariables.EVALUATION_ID];
          return this.experimentDto.id == Number(id);
        }
        return false;
      };
      this.experimentUpdatesSubscription = this.pushService.pushMessageSubscribe(filterPredicate)
        .subscribe({
          next: (pushRequestDto: PushRequestDto) => {
            this.handleExperimentPush(pushRequestDto);
            this.blink = true;
            this.getExperiment();
            this.getExperimentErsReport();
          },
          error: (error) => {
            this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
          }
        });
    }
  }

  private startExperimentProgressTimer(): void {
    if (!this.experimentProgressTimerSubscription) {
      Logger.debug(`Start experiment ${this.experimentDto.requestId} progress timer`);
      this.experimentProgressTimerSubscription = timer(0, this.timerInterval).subscribe({
        next: () => {
          this.currentDate = new Date();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
    }
  }

  private handleExperimentPush(pushRequestDto: PushRequestDto): void {
    const requestStatus = pushRequestDto.additionalProperties[PushVariables.EVALUATION_REQUEST_STATUS];
    if (this.finalRequestStatuses.includes(requestStatus)) {
      this.unSubscribeExperimentUpdates();
    }
  }

  private stopExperimentProgressTimer(): void {
    if (this.experimentProgressTimerSubscription) {
      this.experimentProgressTimerSubscription.unsubscribe();
      this.experimentProgressTimerSubscription = null;
      Logger.debug(`Stop experiment ${this.experimentDto.requestId} timer`);
    }
  }

  private unSubscribeExperimentUpdates(): void {
    if (this.experimentUpdatesSubscription) {
      this.experimentUpdatesSubscription.unsubscribe();
      this.experimentUpdatesSubscription = null;
      Logger.debug(`Unsubscribe experiment ${this.experimentDto.requestId} status change`);
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
      { name: ExperimentFields.CREATED_BY, label: "Пользователь" },
      { name: ExperimentFields.RELATION_NAME, label: "Обучающая выборка:" },
      { name: ExperimentFields.NUM_INSTANCES, label: "Число объектов:" },
      { name: ExperimentFields.NUM_ATTRIBUTES, label: "Число атрибутов:" },
      { name: ExperimentFields.NUM_CLASSES, label: "Число классов:" },
      { name: ExperimentFields.CLASS_NAME, label: "Атрибут класса:" },
      { name: ExperimentFields.EVALUATION_TOTAL_TIME, label: "Время построения эксперимента:" },
      { name: ExperimentFields.CREATION_DATE, label: "Дата создания заявки:" },
      { name: ExperimentFields.START_DATE, label: "Дата начала обработки заявки:" },
      { name: ExperimentFields.END_DATE, label: "Дата окончания обработки заявки:" },
      { name: ExperimentFields.MODEL_PATH, label: "Модель эксперимента:" },
      { name: ExperimentFields.DELETED_DATE, label: "Дата удаления модели:" }
    ];
  }
}

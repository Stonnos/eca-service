import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  ExperimentDto,
  ExperimentErsReportDto,
  ExperimentProgressDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { ActivatedRoute } from "@angular/router";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { saveAs } from 'file-saver/dist/FileSaver';
import { ExperimentFields } from "../../common/util/field-names";
import { FieldLink } from "../../common/model/field-link";
import { FieldService } from "../../common/services/field.service";
import { Utils } from "../../common/util/utils";
import { Subscription, timer } from "rxjs";
import { finalize } from "rxjs/internal/operators";
import { RequestStatus } from "../../common/model/request-status.enum";

@Component({
  selector: 'app-experiment-details',
  templateUrl: './experiment-details.component.html',
  styleUrls: ['./experiment-details.component.scss']
})
export class ExperimentDetailsComponent implements OnInit, OnDestroy, FieldLink {

  private readonly experimentRequestId: string;

  private readonly loadingFieldsMap = new Map<string, boolean>()
    .set(ExperimentFields.TRAINING_DATA_PATH, false)
    .set(ExperimentFields.EXPERIMENT_PATH, false);

  private readonly updateProgressInterval = 1000;
  private readonly updateStatusInterval = 3000;

  public experimentFields: any[] = [];

  public experimentDto: ExperimentDto;

  public experimentErsReport: ExperimentErsReportDto;

  public experimentProgress: ExperimentProgressDto;

  private updateSubscription: Subscription = new Subscription();

  public linkColumns: string[] = [ExperimentFields.TRAINING_DATA_PATH, ExperimentFields.EXPERIMENT_PATH];

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService,
                     private route: ActivatedRoute,
                     private fieldService: FieldService) {
    this.experimentRequestId = this.route.snapshot.params.id;
    this.initExperimentFields();
  }

  public ngOnInit(): void {
    this.getExperiment();
    this.getExperimentErsReport();
  }

  public ngOnDestroy(): void {
    this.unSubscribe();
  }

  public getExperiment(): void {
    this.experimentsService.getExperiment(this.experimentRequestId)
      .subscribe({
        next: (experimentDto: ExperimentDto) => {
          this.experimentDto = experimentDto;
          if (this.experimentDto.requestStatus.value == RequestStatus.NEW) {
            //Subscribe for experiment status change
            this.updateExperimentStatus();
          } else if (this.experimentDto.requestStatus.value == RequestStatus.IN_PROGRESS) {
            //Subscribe for experiment progress change
            this.updateExperimentProgress();
          }
        },
        error: (error) => {
          this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
        }
      });
  }

  public getExperimentTrainingDataFile(): void {
    this.loadingFieldsMap.set(ExperimentFields.TRAINING_DATA_PATH, true);
    this.experimentsService.getExperimentTrainingDataFile(this.experimentDto.requestId)
      .pipe(
        finalize(() => {
          this.loadingFieldsMap.set(ExperimentFields.TRAINING_DATA_PATH, false);
        })
      )
      .subscribe({
        next: (blob: Blob) => {
          saveAs(blob, this.experimentDto.trainingDataAbsolutePath);
        },
        error: (error) => {
          this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
        }
      });
  }

  public getExperimentResultsFile(): void {
    this.loadingFieldsMap.set(ExperimentFields.EXPERIMENT_PATH, true);
    this.experimentsService.getExperimentResultsFile(this.experimentDto.requestId)
      .pipe(
        finalize(() => {
          this.loadingFieldsMap.set(ExperimentFields.EXPERIMENT_PATH, false);
        })
      )
      .subscribe({
        next: (blob: Blob) => {
          saveAs(blob, this.experimentDto.experimentAbsolutePath);
        },
        error: (error) => {
          this.messageService.add({severity: 'error', summary: 'Ошибка', detail: error.message});
        }
      });
  }

  public getExperimentErsReport(): void {
    this.experimentsService.getExperimentErsReport(this.experimentRequestId)
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

  public isLoading(field: string): boolean {
    return this.loadingFieldsMap.has(field) && this.loadingFieldsMap.get(field);
  }

  public onLink(field: string) {
    switch (field) {
      case ExperimentFields.TRAINING_DATA_PATH:
        this.getExperimentTrainingDataFile();
        break;
      case ExperimentFields.EXPERIMENT_PATH:
        this.getExperimentResultsFile();
        break;
      default:
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

  private updateExperimentStatus(): void {
    this.updateSubscription = timer(0, this.updateStatusInterval).subscribe({
      next: () => {
        this.experimentsService.getExperiment(this.experimentRequestId)
          .subscribe({
            next: (experimentDto: ExperimentDto) => {
              if (this.experimentDto.requestStatus.value != experimentDto.requestStatus.value) {
                //Update experiment data if status has been changed
                this.experimentDto = experimentDto;
                if (this.experimentDto.requestStatus.value == RequestStatus.IN_PROGRESS) {
                  //Subscribe for experiment progress change
                  this.unSubscribe();
                  this.updateExperimentProgress();
                } else {
                  this.unSubscribe();
                }
                this.getExperimentErsReport();
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

  private updateExperimentProgress(): void {
    this.updateSubscription = timer(0, this.updateProgressInterval).subscribe({
      next: () => {
        this.experimentsService.getExperimentProgress(this.experimentRequestId)
          .subscribe({
            next: (experimentProgress: ExperimentProgressDto) => {
              if (!experimentProgress.finished) {
                this.experimentProgress = experimentProgress;
              } else {
                this.unSubscribe();
                this.getExperiment();
                this.getExperimentErsReport();
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

  private unSubscribe(): void {
    this.updateSubscription.unsubscribe();
  }

  private initExperimentFields(): void {
    this.experimentFields = [
      { name: ExperimentFields.REQUEST_ID, label: "UUID заявки" },
      { name: ExperimentFields.REQUEST_STATUS_DESCRIPTION, label: "Статус заявки" },
      { name: ExperimentFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности" },
      { name: ExperimentFields.EMAIL, label: "Email заявки" },
      { name: ExperimentFields.EVALUATION_TOTAL_TIME, label: "Время построения эксперимента" },
      { name: ExperimentFields.CREATION_DATE, label: "Дата создания заявки" },
      { name: ExperimentFields.START_DATE, label: "Дата начала эксперимента" },
      { name: ExperimentFields.END_DATE, label: "Дата окончания эксперимента" },
      { name: ExperimentFields.SENT_DATE, label: "Дата отправки результатов" },
      { name: ExperimentFields.DELETED_DATE, label: "Дата удаления результатов" },
      { name: ExperimentFields.TRAINING_DATA_PATH, label: "Обучающая выборка" },
      { name: ExperimentFields.EXPERIMENT_PATH, label: "Результаты эксперимента" }
    ];
  }
}

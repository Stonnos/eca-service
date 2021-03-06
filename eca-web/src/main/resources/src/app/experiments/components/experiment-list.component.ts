import { Component, Injector, OnDestroy, OnInit } from '@angular/core';
import {
  CreateExperimentResultDto,
  ExperimentDto, FilterDictionaryDto, FilterDictionaryValueDto, FilterFieldDto, PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../services/experiments.service";
import { MessageService } from "primeng/api";
import { saveAs } from 'file-saver/dist/FileSaver';
import { BaseListComponent } from "../../common/lists/base-list.component";
import { OverlayPanel } from "primeng/primeng";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";
import { finalize } from "rxjs/internal/operators";
import { ExperimentRequest } from "../../create-experiment/model/experiment-request.model";
import { Router } from "@angular/router";
import { RouterPaths } from "../../routing/router-paths";
import { ExperimentFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { ReportsService } from "../../common/services/report.service";
import { EvaluationMethod } from "../../common/model/evaluation-method.enum";
import { Utils } from "../../common/util/utils";
import { ReportType } from "../../common/model/report-type.enum";
import { WsService } from "../../common/websockets/ws.service";
import { Subscription } from "rxjs";
import { RequestStatus } from "../../common/model/request-status.enum";

@Component({
  selector: 'app-experiment-list',
  templateUrl: './experiment-list.component.html',
  styleUrls: ['./experiment-list.component.scss']
})
export class ExperimentListComponent extends BaseListComponent<ExperimentDto> implements OnInit, OnDestroy {

  private static readonly EXPERIMENTS_REPORT_FILE_NAME = 'experiments-report.xlsx';

  private wsService: WsService;

  private experimentsUpdatesSubscriptions: Subscription;

  public requestStatusStatisticsDto: RequestStatusStatisticsDto;

  public createExperimentDialogVisibility: boolean = false;

  public selectedExperiment: ExperimentDto;
  public selectedColumn: string;

  public experimentTypes: FilterDictionaryValueDto[] = [];
  public evaluationMethods: FilterDictionaryValueDto[] = [];

  public experimentRequest: ExperimentRequest = new ExperimentRequest();

  public constructor(private injector: Injector,
                     private experimentsService: ExperimentsService,
                     private filterService: FilterService,
                     private reportsService: ReportsService,
                     private router: Router) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = ExperimentFields.CREATION_DATE;
    this.linkColumns = [ExperimentFields.TRAINING_DATA_PATH, ExperimentFields.EXPERIMENT_PATH,
      ExperimentFields.REQUEST_ID, ExperimentFields.EVALUATION_METHOD_DESCRIPTION];
    this.notSortableColumns = [ExperimentFields.TRAINING_DATA_PATH, ExperimentFields.EXPERIMENT_PATH, ExperimentFields.EVALUATION_TOTAL_TIME];
    this.initColumns();
  }

  public ngOnInit(): void {
    this.getFilterFields();
    this.getRequestStatusesStatistics();
    this.getEvaluationMethods();
    this.getExperimentTypes();
    this.subscribeForExperimentsUpdates();
  }

  public ngOnDestroy(): void {
    if (this.experimentsUpdatesSubscriptions) {
      this.experimentsUpdatesSubscriptions.unsubscribe();
    }
    if (this.wsService) {
      this.wsService.close();
    }
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ExperimentDto>> {
    return this.experimentsService.getExperiments(pageRequest);
  }

  public getRequestStatusesStatistics() {
    this.experimentsService.getRequestStatusesStatistics().subscribe({
      next: (requestStatusStatisticsDto: RequestStatusStatisticsDto) => {
        this.requestStatusStatisticsDto = requestStatusStatisticsDto;
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    });
  }

  public generateReport() {
    const observable = this.reportsService.getBaseReport(this.pageRequestDto, ReportType.EXPERIMENTS);
    this.downloadReport(observable, ExperimentListComponent.EXPERIMENTS_REPORT_FILE_NAME);
  }

  public getNumFolds(experimentDto: ExperimentDto): number {
    return this.fieldService.getFieldValue(ExperimentFields.NUM_FOLDS, experimentDto, Utils.MISSING_VALUE);
  }

  public getNumTests(experimentDto: ExperimentDto): number {
    return this.fieldService.getFieldValue(ExperimentFields.NUM_TESTS, experimentDto, Utils.MISSING_VALUE);
  }

  public getSeed(experimentDto: ExperimentDto): number {
    return this.fieldService.getFieldValue(ExperimentFields.SEED, experimentDto, Utils.MISSING_VALUE);
  }

  public onLink(event, column: string, experiment: ExperimentDto, overlayPanel: OverlayPanel) {
    switch (column) {
      case ExperimentFields.TRAINING_DATA_PATH:
        this.getExperimentTrainingDataFile(experiment);
        break;
      case ExperimentFields.EXPERIMENT_PATH:
        this.getExperimentResultsFile(experiment);
        break;
      case ExperimentFields.REQUEST_ID:
        this.router.navigate([RouterPaths.EXPERIMENT_DETAILS_URL, experiment.requestId]);
        break;
      case ExperimentFields.EVALUATION_METHOD_DESCRIPTION:
        if (experiment.evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION) {
          this.toggleOverlayPanel(event, experiment, column, overlayPanel);
        }
        break;
      default:
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${column} as link`});
    }
  }

  public getExperimentTrainingDataFile(experiment: ExperimentDto): void {
    this.loading = true;
    this.experimentsService.getExperimentTrainingDataFile(experiment.requestId)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (blob: Blob) => {
          saveAs(blob, experiment.trainingDataAbsolutePath);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getExperimentResultsFile(experiment: ExperimentDto): void {
    this.loading = true;
    this.experimentsService.getExperimentResultsFile(experiment.requestId)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (blob: Blob) => {
          saveAs(blob, experiment.experimentAbsolutePath);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public onCreateExperimentDialogVisibility(visible): void {
    this.createExperimentDialogVisibility = visible;
  }

  public onCreateExperiment(experimentRequest: ExperimentRequest): void {
    this.loading = true;
    this.experimentsService.createExperiment(experimentRequest)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (result: CreateExperimentResultDto) => {
          if (result.created) {
            this.messageService.add({ severity: 'success', summary: `Эксперимент был успешно создан`, detail: '' });
            this.lastCreatedId = result.requestId;
            this.getRequestStatusesStatistics();
            this.reloadPageWithLoader();
          } else {
            this.messageService.add({ severity: 'error', summary: 'Не удалось создать эксперимент', detail: result.errorMessage });
          }
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public showCreateExperimentDialog(): void {
    this.createExperimentDialogVisibility = true;
  }

  public getFilterFields() {
    this.filterService.getExperimentFilterFields()
      .subscribe({
        next: (filterFields: FilterFieldDto[]) => {
          this.filters = this.filterService.mapToFilters(filterFields);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getExperimentTypes(): void {
    this.filterService.getExperimentTypeDictionary()
      .subscribe({
        next: (filterDictionary: FilterDictionaryDto) => {
          this.experimentTypes = filterDictionary.values;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getEvaluationMethods(): void {
    this.filterService.getEvaluationMethodDictionary()
      .subscribe({
        next: (filterDictionary: FilterDictionaryDto) => {
          this.evaluationMethods = filterDictionary.values;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public isBlink(item: any): boolean {
    return this.blinkId && this.blinkId == item.requestId;
  }

  private toggleOverlayPanel(event, experimentDto: ExperimentDto, column: string, overlayPanel: OverlayPanel): void {
    this.selectedExperiment = experimentDto;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  private subscribeForExperimentsUpdates(): void {
    this.wsService = new WsService();
    this.experimentsUpdatesSubscriptions = this.wsService.subscribe('/queue/experiment')
      .subscribe({
        next: (message) => {
          const experimentDto: ExperimentDto = JSON.parse(message.body);
          this.lastCreatedId = experimentDto.requestId;
          this.showMessage(experimentDto);
          this.reloadPage(false);
          this.getRequestStatusesStatistics();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private showMessage(experimentDto: ExperimentDto): void {
    if (experimentDto.requestStatus.value == RequestStatus.NEW) {
      this.messageService.add({ severity: 'info', summary: `Поступила новая заявка на эксперимент ${experimentDto.requestId}`, detail: '' });
    } else if (experimentDto.requestStatus.value == RequestStatus.FINISHED) {
      this.messageService.add({ severity: 'info', summary: `Эксперимент ${experimentDto.requestId} успешно завершен`, detail: '' });
    }
  }

  private initColumns() {
    this.columns = [
      { name: ExperimentFields.REQUEST_ID, label: "UUID заявки" },
      { name: ExperimentFields.EXPERIMENT_TYPE_DESCRIPTION, label: "Тип эксперимента", sortBy: ExperimentFields.EXPERIMENT_TYPE },
      { name: ExperimentFields.REQUEST_STATUS_DESCRIPTION, label: "Статус заявки", sortBy: ExperimentFields.REQUEST_STATUS },
      { name: ExperimentFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности", sortBy: ExperimentFields.EVALUATION_METHOD },
      { name: ExperimentFields.FIRST_NAME, label: "Имя заявки" },
      { name: ExperimentFields.EMAIL, label: "Email заявки" },
      { name: ExperimentFields.TRAINING_DATA_PATH, label: "Обучающая выборка" },
      { name: ExperimentFields.EXPERIMENT_PATH, label: "Результаты эксперимента" },
      { name: ExperimentFields.EVALUATION_TOTAL_TIME, label: "Время построения эксперимента" },
      { name: ExperimentFields.CREATION_DATE, label: "Дата создания заявки" },
      { name: ExperimentFields.START_DATE, label: "Дата начала эксперимента" },
      { name: ExperimentFields.END_DATE, label: "Дата окончания эксперимента" },
      { name: ExperimentFields.SENT_DATE, label: "Дата отправки результатов" },
      { name: ExperimentFields.DELETED_DATE, label: "Дата удаления результатов" }
    ];
  }
}

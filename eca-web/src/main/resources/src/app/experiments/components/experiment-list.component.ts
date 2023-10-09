import { Component, Injector, OnDestroy, OnInit } from '@angular/core';
import {
  CreateExperimentResultDto,
  ExperimentDto, FilterDictionaryDto, FilterDictionaryValueDto, FilterFieldDto, PageDto,
  PageRequestDto, PushRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../services/experiments.service";
import { OverlayPanel } from "primeng/primeng";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";
import { finalize } from "rxjs/internal/operators";
import { ExperimentRequest } from "../../create-experiment/model/experiment-request.model";
import { Router } from "@angular/router";
import { RouterPaths } from "../../routing/router-paths";
import { ExperimentFields } from "../../common/util/field-names";
import { ReportsService } from "../../common/services/report.service";
import { EvaluationMethod } from "../../common/model/evaluation-method.enum";
import { ReportType } from "../../common/model/report-type.enum";
import { Subscription } from "rxjs";
import { ValidationService } from "../../common/services/validation.service";
import { ValidationErrorCode } from "../../common/model/validation-error-code";
import { PushVariables } from "../../common/util/push-variables";
import { PushService } from "../../common/push/push.service";
import { PushMessageType } from "../../common/util/push-message.type";
import { Logger } from "../../common/util/logging";
import { CreateExperimentRequestDto } from "../../create-experiment/model/create-experiment-request.model";
import { ErrorHandler } from "../../common/services/error-handler";
import { InstancesInfoService } from "../../common/instances-info/services/instances-info.service";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { InstancesInfoFilterValueTransformer } from "../../filter/autocomplete/transformer/instances-info-filter-value-transformer";
import { InstancesInfoAutocompleteHandler } from "../../filter/autocomplete/handler/instances-info-autocomplete-handler";
import { MessageService } from "primeng/api";
import { FieldService } from "../../common/services/field.service";
import { ExperimentFilterFields } from "../../common/util/filter-field-names";

@Component({
  selector: 'app-experiment-list',
  templateUrl: './experiment-list.component.html',
  styleUrls: ['./experiment-list.component.scss']
})
export class ExperimentListComponent extends BaseListComponent<ExperimentDto> implements OnInit, OnDestroy {

  private static readonly EXPERIMENTS_REPORT_FILE_NAME = 'experiments-report.xlsx';

  private readonly errorCodes: string[] = [
    ValidationErrorCode.CLASS_ATTRIBUTE_NOT_SELECTED,
    ValidationErrorCode.INSTANCES_NOT_FOUND,
    ValidationErrorCode.SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW,
    ValidationErrorCode.CLASS_VALUES_IS_TOO_LOW
  ];

  private readonly errorCodesMap = new Map<string, string>()
    .set(ValidationErrorCode.CLASS_ATTRIBUTE_NOT_SELECTED, 'Не выбран атрибут класса для заданной обучающей выборки')
    .set(ValidationErrorCode.INSTANCES_NOT_FOUND, 'Обучающая выборка не найдена')
    .set(ValidationErrorCode.SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW, 'Выберите не менее двух атрибутов классификации')
    .set(ValidationErrorCode.CLASS_VALUES_IS_TOO_LOW, 'Число классов должно быть не менее двух');

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
                     private validationService: ValidationService,
                     private errorHandler: ErrorHandler,
                     private pushService: PushService,
                     private instancesInfoService: InstancesInfoService,
                     private router: Router) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = ExperimentFields.CREATION_DATE;
    this.linkColumns = [ExperimentFields.RELATION_NAME, ExperimentFields.MODEL_PATH,
      ExperimentFields.REQUEST_ID, ExperimentFields.EVALUATION_METHOD_DESCRIPTION];
    this.notSortableColumns = [ExperimentFields.EVALUATION_TOTAL_TIME];
    this.initColumns();
  }

  public ngOnInit(): void {
    this.addLazyReferenceTransformers(new InstancesInfoFilterValueTransformer());
    this.addAutoCompleteHandler(new InstancesInfoAutocompleteHandler(this.instancesInfoService, this.messageService));
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

  public onLink(event, column: string, experiment: ExperimentDto, overlayPanel: OverlayPanel) {
    switch (column) {
      case ExperimentFields.RELATION_NAME:
        if (experiment.instancesInfo) {
          this.toggleOverlayPanel(event, experiment, column, overlayPanel);
        }
        break;
      case ExperimentFields.MODEL_PATH:
        this.downloadExperimentResults(experiment);
        break;
      case ExperimentFields.REQUEST_ID:
        this.router.navigate([RouterPaths.EXPERIMENT_DETAILS_URL, experiment.id]);
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

  public downloadExperimentResults(experiment: ExperimentDto): void {
    this.loading = true;
    this.experimentsService.downloadExperimentResults(experiment,
      () => this.loading = false,
      (error) => this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message })
    );
  }

  public onCreateExperimentDialogVisibility(visible): void {
    this.createExperimentDialogVisibility = visible;
  }

  public onCreateExperiment(experimentRequest: ExperimentRequest): void {
    const createExperimentRequestDto =
      new CreateExperimentRequestDto(experimentRequest.instancesUuid, experimentRequest.experimentType.value, experimentRequest.evaluationMethod.value)
    this.loading = true;
    this.experimentsService.createExperiment(createExperimentRequestDto)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (createExperimentResultDto: CreateExperimentResultDto) => {
          this.handleExperimentCreated(createExperimentResultDto);
        },
        error: (error) => {
          this.handleCreateExperimentError(error);
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
    return this.blinkId && this.blinkId == item.id;
  }

  private toggleOverlayPanel(event, experimentDto: ExperimentDto, column: string, overlayPanel: OverlayPanel): void {
    this.selectedExperiment = experimentDto;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  private subscribeForExperimentsUpdates(): void {
    const filterPredicate = (pushRequestDto: PushRequestDto) => pushRequestDto.pushType == 'SYSTEM' && pushRequestDto.messageType == PushMessageType.EXPERIMENT_STATUS_CHANGE;
    this.experimentsUpdatesSubscriptions = this.pushService.pushMessageSubscribe(filterPredicate)
      .subscribe({
        next: (pushRequestDto: PushRequestDto) => {
          if (!this.lastCreatedId) {
            this.lastCreatedId = pushRequestDto.additionalProperties[PushVariables.EVALUATION_ID];
            this.reloadPage(false);
            this.getRequestStatusesStatistics();
          }
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private handleCreateExperimentError(error): void {
    const errorCode = this.errorHandler.getFirstErrorCode(error, this.errorCodes);
    if (errorCode) {
      const errorMessage = this.errorCodesMap.get(errorCode);
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: errorMessage });
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error });
    }
  }

  private handleExperimentCreated(createExperimentResultDto: CreateExperimentResultDto): void {
    Logger.debug(`Experiment ${createExperimentResultDto.requestId} has been created`);
    if (!this.lastCreatedId) {
      this.lastCreatedId = createExperimentResultDto.id;
      this.getRequestStatusesStatistics();
      this.reloadPageWithLoader();
    }
  }

  private initColumns() {
    this.columns = [
      { name: ExperimentFields.REQUEST_ID, label: "UUID заявки", sortBy: ExperimentFilterFields.REQUEST_ID },
      { name: ExperimentFields.EXPERIMENT_TYPE_DESCRIPTION, label: "Тип эксперимента", sortBy: ExperimentFilterFields.EXPERIMENT_TYPE },
      { name: ExperimentFields.REQUEST_STATUS_DESCRIPTION, label: "Статус заявки", sortBy: ExperimentFilterFields.REQUEST_STATUS },
      { name: ExperimentFields.MAX_PCT_CORRECT, label: "Точность наличшего классификатора, %", sortBy: ExperimentFilterFields.MAX_PCT_CORRECT },
      { name: ExperimentFields.RELATION_NAME, label: "Обучающая выборка", sortBy: ExperimentFilterFields.RELATION_NAME },
      { name: ExperimentFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности", sortBy: ExperimentFilterFields.EVALUATION_METHOD },
      { name: ExperimentFields.CREATED_BY, label: "Пользователь", sortBy: ExperimentFilterFields.CREATED_BY },
      { name: ExperimentFields.MODEL_PATH, label: "Модель эксперимента", sortBy: ExperimentFilterFields.MODEL_PATH },
      { name: ExperimentFields.EVALUATION_TOTAL_TIME, label: "Время построения эксперимента" },
      { name: ExperimentFields.CREATION_DATE, label: "Дата создания заявки", sortBy: ExperimentFilterFields.CREATION_DATE },
      { name: ExperimentFields.START_DATE, label: "Дата начала эксперимента", sortBy: ExperimentFilterFields.START_DATE },
      { name: ExperimentFields.END_DATE, label: "Дата окончания эксперимента", sortBy: ExperimentFilterFields.END_DATE },
      { name: ExperimentFields.DELETED_DATE, label: "Дата удаления модели", sortBy: ExperimentFilterFields.DELETED_DATE }
    ];
  }
}

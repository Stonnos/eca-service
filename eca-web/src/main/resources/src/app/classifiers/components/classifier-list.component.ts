import { Component, Injector } from '@angular/core';
import {
  EvaluationLogDto, FilterFieldDto,
  PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ClassifiersService } from "../services/classifiers.service";
import { OverlayPanel } from "primeng/primeng";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";
import { EvaluationMethod } from "../../common/model/evaluation-method.enum";
import { Router } from "@angular/router";
import { RouterPaths } from "../../routing/router-paths";
import { EvaluationLogFields } from "../../common/util/field-names";
import { ReportsService } from "../../common/services/report.service";
import { ReportType } from "../../common/model/report-type.enum";
import { InstancesInfoService } from "../../common/instances-info/services/instances-info.service";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { MessageService } from "primeng/api";
import { FieldService } from "../../common/services/field.service";
import { InstancesInfoFilterValueTransformer } from "../../filter/autocomplete/transformer/instances-info-filter-value-transformer";
import { InstancesInfoAutocompleteHandler } from "../../filter/autocomplete/handler/instances-info-autocomplete-handler";
import { EvaluationLogFilterFields } from "../../common/util/filter-field-names";

@Component({
  selector: 'app-classifier-list',
  templateUrl: './classifier-list.component.html',
  styleUrls: ['./classifier-list.component.scss']
})
export class ClassifierListComponent extends BaseListComponent<EvaluationLogDto> {

  private static readonly EVALUATION_LOGS_REPORT_FILE_NAME = 'evaluation-logs-report.xlsx';

  public requestStatusStatisticsDto: RequestStatusStatisticsDto;

  public selectedEvaluationLog: EvaluationLogDto;
  public selectedColumn: string;

  public constructor(private injector: Injector,
                     private classifiersService: ClassifiersService,
                     private filterService: FilterService,
                     private reportsService: ReportsService,
                     private instancesInfoService: InstancesInfoService,
                     private router: Router) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = EvaluationLogFields.CREATION_DATE;
    this.linkColumns = [EvaluationLogFields.CLASSIFIER_DESCRIPTION, EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION,
      EvaluationLogFields.RELATION_NAME, EvaluationLogFields.REQUEST_ID, EvaluationLogFields.MODEL_PATH];
    this.notSortableColumns = [EvaluationLogFields.EVALUATION_TOTAL_TIME];
    this.initColumns();
  }

  public ngOnInit() {
    this.addLazyReferenceTransformers(new InstancesInfoFilterValueTransformer());
    this.addAutoCompleteHandler(new InstancesInfoAutocompleteHandler(this.instancesInfoService, this.messageService));
    this.getFilterFields();
    this.getRequestStatusesStatistics();
  }

  public getFilterFields() {
    this.filterService.getEvaluationLogFilterFields()
      .subscribe({
        next: (filterFields: FilterFieldDto[]) => {
          this.filters = this.filterService.mapToFilters(filterFields);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<EvaluationLogDto>> {
    return this.classifiersService.getEvaluations(pageRequest);
  }

  public getRequestStatusesStatistics() {
    this.classifiersService.getRequestStatusesStatistics()
      .subscribe({
        next: (requestStatusStatisticsDto: RequestStatusStatisticsDto) => {
          this.requestStatusStatisticsDto = requestStatusStatisticsDto;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public generateReport() {
    const observable = this.reportsService.getBaseReport(this.pageRequestDto, ReportType.EVALUATION_LOGS);
    this.downloadReport(observable, ClassifierListComponent.EVALUATION_LOGS_REPORT_FILE_NAME);
  }

  public onSelect(event, evaluationLog: EvaluationLogDto, column: string, overlayPanel: OverlayPanel): void {
    switch (column) {
      case EvaluationLogFields.REQUEST_ID:
        this.router.navigate([RouterPaths.EVALUATION_DETAILS_URL, evaluationLog.id]);
        break;
      case EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION:
        if (evaluationLog.evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION) {
          this.toggleOverlayPanel(event, evaluationLog, column, overlayPanel);
        }
        break;
      case EvaluationLogFields.MODEL_PATH:
        this.downloadModel(evaluationLog);
        break;
      default:
        this.toggleOverlayPanel(event, evaluationLog, column, overlayPanel);
    }
  }

  public downloadModel(evaluationLogDto: EvaluationLogDto): void {
    this.loading = true;
    this.classifiersService.downloadModel(evaluationLogDto,
      () => this.loading = false,
      (error) => this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message })
    );
  }

  private toggleOverlayPanel(event, evaluationLog: EvaluationLogDto, column: string, overlayPanel: OverlayPanel): void {
    this.selectedEvaluationLog = evaluationLog;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  private initColumns() {
    this.columns = [
      { name: EvaluationLogFields.REQUEST_ID, label: "UUID заявки", sortBy: EvaluationLogFilterFields.REQUEST_ID },
      { name: EvaluationLogFields.CLASSIFIER_DESCRIPTION, label: "Классификатор", sortBy: EvaluationLogFilterFields.CLASSIFIER_NAME },
      { name: EvaluationLogFields.REQUEST_STATUS_DESCRIPTION, label: "Статус заявки", sortBy: EvaluationLogFilterFields.REQUEST_STATUS },
      { name: EvaluationLogFields.PCT_CORRECT, label: "Точность классификатора, %", sortBy: EvaluationLogFilterFields.PCT_CORRECT },
      { name: EvaluationLogFields.RELATION_NAME, label: "Обучающая выборка", sortBy: EvaluationLogFilterFields.RELATION_NAME },
      { name: EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности", sortBy: EvaluationLogFilterFields.EVALUATION_METHOD },
      { name: EvaluationLogFields.CREATED_BY, label: "Пользователь", sortBy: EvaluationLogFilterFields.CREATED_BY },
      { name: EvaluationLogFields.MODEL_PATH, label: "Модель классификатора", sortBy: EvaluationLogFilterFields.MODEL_PATH },
      { name: EvaluationLogFields.EVALUATION_TOTAL_TIME, label: "Время построения модели" },
      { name: EvaluationLogFields.CREATION_DATE, label: "Дата создания заявки", sortBy: EvaluationLogFilterFields.CREATION_DATE },
      { name: EvaluationLogFields.START_DATE, label: "Дата начала построения модели", sortBy: EvaluationLogFilterFields.START_DATE },
      { name: EvaluationLogFields.END_DATE, label: "Дата окончания построения модели", sortBy: EvaluationLogFilterFields.END_DATE },
      { name: EvaluationLogFields.DELETED_DATE, label: "Дата удаления модели", sortBy: EvaluationLogFilterFields.DELETED_DATE }
    ];
  }
}

import { Component, Injector } from '@angular/core';
import {
  EvaluationLogDto, FilterFieldDto, InstancesInfoDto,
  PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { MessageService } from "primeng/api";
import { ClassifiersService } from "../services/classifiers.service";
import { OverlayPanel } from "primeng/primeng";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";
import { EvaluationMethod } from "../../common/model/evaluation-method.enum";
import { Router } from "@angular/router";
import { RouterPaths } from "../../routing/router-paths";
import { EvaluationLogFields, InstancesInfoDtoFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { ReportsService } from "../../common/services/report.service";
import { ReportType } from "../../common/model/report-type.enum";
import { InstancesInfoService } from "../../common/instances-info/services/instances-info.service";
import { AutocompleteItemModel } from "../../filter/model/autocomplete-item.model";
import { Filter } from "../../filter/model/filter.model";

@Component({
  selector: 'app-classifier-list',
  templateUrl: './classifier-list.component.html',
  styleUrls: ['./classifier-list.component.scss']
})
export class ClassifierListComponent extends BaseListComponent<EvaluationLogDto> {

  private static readonly EVALUATION_LOGS_REPORT_FILE_NAME = 'evaluation-logs-report.xlsx';

  private instancesPageSize: number = 100;

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

  public onFilterFieldAutocomplete(autocompleteItemModel: AutocompleteItemModel): void {
    if (autocompleteItemModel.filterField == 'instancesInfo.id') {
      const pageRequest: PageRequestDto = {
        page: 0,
        size: this.instancesPageSize,
        sortField: InstancesInfoDtoFields.CREATED_DATE,
        ascending: false,
        searchQuery: null,
        filters: [
          {
            name: InstancesInfoDtoFields.RELATION_NAME,
            values: [autocompleteItemModel.searchQuery],
            matchMode: 'LIKE'
          }
        ]
      };
      this.instancesInfoService.getInstancesInfoPage(pageRequest)
        .subscribe({
          next: (instancesInfoPage: PageDto<InstancesInfoDto>) => {
            const filter = this.filters.filter((item: Filter) => item.name == autocompleteItemModel.filterField).pop();
            filter.values = instancesInfoPage.content.map((item: InstancesInfoDto) => {
              return {
                label: item.relationName,
                value: item
              }
            });
          },
          error: (error) => {
            this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
          }
        });
    }
  }

  protected tramsFormLazyReferenceValue(filter: Filter, values: any[]): string[] {
    if (filter.name == 'instancesInfo.id') {
      return values.map((item) => item.value.id);
    }
    return super.tramsFormLazyReferenceValue(filter, values);
  }

  private toggleOverlayPanel(event, evaluationLog: EvaluationLogDto, column: string, overlayPanel: OverlayPanel): void {
    this.selectedEvaluationLog = evaluationLog;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  private initColumns() {
    this.columns = [
      { name: EvaluationLogFields.REQUEST_ID, label: "UUID заявки" },
      { name: EvaluationLogFields.CLASSIFIER_DESCRIPTION, label: "Классификатор", sortBy: EvaluationLogFields.CLASSIFIER_NAME },
      { name: EvaluationLogFields.REQUEST_STATUS_DESCRIPTION, label: "Статус заявки", sortBy: EvaluationLogFields.REQUEST_STATUS },
      { name: EvaluationLogFields.PCT_CORRECT, label: "Точность классификатора" },
      { name: EvaluationLogFields.RELATION_NAME, label: "Обучающая выборка" },
      { name: EvaluationLogFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности", sortBy: EvaluationLogFields.EVALUATION_METHOD },
      { name: EvaluationLogFields.MODEL_PATH, label: "Модель классификатора" },
      { name: EvaluationLogFields.EVALUATION_TOTAL_TIME, label: "Время построения модели" },
      { name: EvaluationLogFields.CREATION_DATE, label: "Дата создания заявки" },
      { name: EvaluationLogFields.START_DATE, label: "Дата начала построения модели" },
      { name: EvaluationLogFields.END_DATE, label: "Дата окончания построения модели" },
      { name: EvaluationLogFields.DELETED_DATE, label: "Дата удаления модели" }
    ];
  }
}

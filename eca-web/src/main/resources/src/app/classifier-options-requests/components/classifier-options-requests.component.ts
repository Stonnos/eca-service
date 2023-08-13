import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifierOptionsRequestDto, FilterFieldDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { OverlayPanel} from "primeng/primeng";
import { ClassifierOptionsRequestService } from "../services/classifier-options-request.service";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";
import { ClassifierOptionsRequestsFields } from "../../common/util/field-names";
import { ReportType } from "../../common/model/report-type.enum";
import { ReportsService } from "../../common/services/report.service";
import { InstancesInfoService } from "../../common/instances-info/services/instances-info.service";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { MessageService } from "primeng/api";
import { FieldService } from "../../common/services/field.service";
import { InstancesInfoFilterValueTransformer } from "../../filter/autocomplete/transformer/instances-info-filter-value-transformer";
import { InstancesInfoAutocompleteHandler } from "../../filter/autocomplete/handler/instances-info-autocomplete-handler";
import { ClassifierOptionsRequestsFilterFields } from "../../common/util/filter-field-names";

@Component({
  selector: 'app-classifier-options-requests',
  templateUrl: './classifier-options-requests.component.html',
  styleUrls: ['./classifier-options-requests.component.scss']
})
export class ClassifierOptionsRequestsComponent extends BaseListComponent<ClassifierOptionsRequestDto> implements OnInit {

  private static readonly CLASSIFIER_OPTIONS_REQUESTS_REPORT_FILE_NAME = 'classifier-options-requests-report.xlsx';

  public selectedRequest: ClassifierOptionsRequestDto;
  public selectedColumn: string;

  public constructor(injector: Injector,
                     private classifierOptionsService: ClassifierOptionsRequestService,
                     private filterService: FilterService,
                     private reportsService: ReportsService,
                     private instancesInfoService: InstancesInfoService,) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = ClassifierOptionsRequestsFields.REQUEST_DATE;
    this.linkColumns = [ClassifierOptionsRequestsFields.RELATION_NAME,
      ClassifierOptionsRequestsFields.CLASSIFIER_NAME, ClassifierOptionsRequestsFields.EVALUATION_METHOD_DESCRIPTION];
    this.notSortableColumns = [ClassifierOptionsRequestsFields.CLASSIFIER_NAME];
    this.initColumns();
  }

  public ngOnInit() {
    this.addLazyReferenceTransformers(new InstancesInfoFilterValueTransformer());
    this.addAutoCompleteHandler(new InstancesInfoAutocompleteHandler(this.instancesInfoService, this.messageService));
    this.getFilterFields();
  }

  public getFilterFields() {
    this.filterService.getClassifierOptionsRequestFilterFields()
      .subscribe({
        next: (filterFields: FilterFieldDto[]) => {
          this.filters = this.filterService.mapToFilters(filterFields);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsRequestDto>> {
    return this.classifierOptionsService.getClassifiersOptionsRequests(pageRequest);
  }

  public generateReport() {
    const observable = this.reportsService.getBaseReport(this.pageRequestDto, ReportType.CLASSIFIERS_OPTIONS_REQUESTS);
    this.downloadReport(observable, ClassifierOptionsRequestsComponent.CLASSIFIER_OPTIONS_REQUESTS_REPORT_FILE_NAME);
  }

  public getColumnValue(column: string, item: ClassifierOptionsRequestDto) {
    if (column == ClassifierOptionsRequestsFields.CLASSIFIER_NAME) {
      return item.classifierInfo && item.classifierInfo.classifierDescription;
    } else {
      return super.getColumnValue(column, item);
    }
  }

  public onSelect(event, classifierOptionsRequestDto: ClassifierOptionsRequestDto, column: string, overlayPanel: OverlayPanel) {
    this.selectedRequest = classifierOptionsRequestDto;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  private initColumns() {
    this.columns = [
      { name: ClassifierOptionsRequestsFields.REQUEST_ID, label: "UUID заявки", sortBy: ClassifierOptionsRequestsFilterFields.REQUEST_ID },
      { name: ClassifierOptionsRequestsFields.RELATION_NAME, label: "Обучающая выборка", sortBy: ClassifierOptionsRequestsFilterFields.RELATION_NAME },
      { name: ClassifierOptionsRequestsFields.CLASSIFIER_NAME, label: "Классификатор" },
      { name: ClassifierOptionsRequestsFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности", sortBy: ClassifierOptionsRequestsFilterFields.EVALUATION_METHOD },
      { name: ClassifierOptionsRequestsFields.REQUEST_DATE, label: "Дата отправки запроса в ERS", sortBy: ClassifierOptionsRequestsFilterFields.REQUEST_DATE },
      { name: ClassifierOptionsRequestsFields.RESPONSE_STATUS_DESCRIPTION, label: "Статус ответа от ERS", sortBy: ClassifierOptionsRequestsFilterFields.RESPONSE_STATUS }
    ];
  }
}

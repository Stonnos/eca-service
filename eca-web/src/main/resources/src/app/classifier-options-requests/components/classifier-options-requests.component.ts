import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifierOptionsRequestDto, FilterFieldDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { OverlayPanel} from "primeng/primeng";
import { JsonPipe } from "@angular/common";
import { ClassifierOptionsRequestService } from "../services/classifier-options-request.service";
import { Observable } from "rxjs/internal/Observable";
import { saveAs } from 'file-saver/dist/FileSaver';
import { FilterService } from "../../filter/services/filter.service";
import { ClassifierOptionsRequestsFields } from "../../common/util/field-names";
import { ReportType } from "../../common/model/report-type.enum";
import { ReportsService } from "../../common/services/report.service";
import { BaseEvaluationListComponent } from "../../common/lists/base-evaluation-list.component";
import { InstancesInfoService } from "../../common/instances-info/services/instances-info.service";

declare var Prism: any;

@Component({
  selector: 'app-classifier-options-requests',
  templateUrl: './classifier-options-requests.component.html',
  styleUrls: ['./classifier-options-requests.component.scss']
})
export class ClassifierOptionsRequestsComponent extends BaseEvaluationListComponent<ClassifierOptionsRequestDto> implements OnInit {

  private static readonly CLASSIFIER_OPTIONS_REQUESTS_REPORT_FILE_NAME = 'classifier-options-requests-report.xlsx';

  public selectedRequest: ClassifierOptionsRequestDto;
  public selectedColumn: string;

  public constructor(injector: Injector,
                     private classifierOptionsService: ClassifierOptionsRequestService,
                     private filterService: FilterService,
                     private reportsService: ReportsService) {
    super(injector, injector.get(InstancesInfoService));
    this.defaultSortField = ClassifierOptionsRequestsFields.REQUEST_DATE;
    this.linkColumns = [ClassifierOptionsRequestsFields.RELATION_NAME,
      ClassifierOptionsRequestsFields.CLASSIFIER_NAME, ClassifierOptionsRequestsFields.EVALUATION_METHOD_DESCRIPTION];
    this.notSortableColumns = [ClassifierOptionsRequestsFields.CLASSIFIER_NAME];
    this.initColumns();
  }

  public ngOnInit() {
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

  public saveClassifierOptions() {
    if (this.selectedRequest) {
      const classifierConfig = this.selectedRequest.classifierInfo.classifierOptionsJson;
      if (classifierConfig) {
        let blob: Blob = new Blob([classifierConfig], {type: 'application/json'});
        saveAs(blob, `${this.selectedRequest.classifierInfo.classifierName}_${this.selectedRequest.requestId}.json`);
      }
    }
  }

  public onSelect(event, classifierOptionsRequestDto: ClassifierOptionsRequestDto, column: string, overlayPanel: OverlayPanel) {
    this.selectedRequest = classifierOptionsRequestDto;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  public getFormattedJsonConfig(): string {
    if (this.selectedRequest.classifierInfo) {
      const classifierConfig = this.selectedRequest.classifierInfo.classifierOptionsJson;
      if (classifierConfig) {
        const configObj = JSON.parse(classifierConfig);
        const json = new JsonPipe().transform(configObj);
        return Prism.highlight(json, Prism.languages['json']);
      }
    }
    return null;
  }

  private initColumns() {
    this.columns = [
      { name: ClassifierOptionsRequestsFields.REQUEST_ID, label: "UUID заявки" },
      { name: ClassifierOptionsRequestsFields.RELATION_NAME, label: "Обучающая выборка" },
      { name: ClassifierOptionsRequestsFields.CLASSIFIER_NAME, label: "Классификатор" },
      { name: ClassifierOptionsRequestsFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности", sortBy: ClassifierOptionsRequestsFields.EVALUATION_METHOD },
      { name: ClassifierOptionsRequestsFields.REQUEST_DATE, label: "Дата отправки запроса в ERS" },
      { name: ClassifierOptionsRequestsFields.RESPONSE_STATUS_DESCRIPTION, label: "Статус ответа от ERS", sortBy: ClassifierOptionsRequestsFields.RESPONSE_STATUS }
    ];
  }
}

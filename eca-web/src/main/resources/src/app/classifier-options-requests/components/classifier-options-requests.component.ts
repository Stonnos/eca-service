import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifierOptionsRequestDto, FilterFieldDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { OverlayPanel} from "primeng/primeng";
import { JsonPipe } from "@angular/common";
import { ClassifierOptionsRequestService } from "../services/classifier-options-request.service";
import { Observable } from "rxjs/internal/Observable";
import { saveAs } from 'file-saver/dist/FileSaver';
import { FilterService } from "../../filter/services/filter.service";
import { ClassifierOptionsRequestsFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";

declare var Prism: any;

@Component({
  selector: 'app-classifier-options-requests',
  templateUrl: './classifier-options-requests.component.html',
  styleUrls: ['./classifier-options-requests.component.scss']
})
export class ClassifierOptionsRequestsComponent extends BaseListComponent<ClassifierOptionsRequestDto> implements OnInit {

  public selectedRequest: ClassifierOptionsRequestDto;
  public selectedColumn: string;

  public constructor(private injector: Injector,
                     private classifierOptionsService: ClassifierOptionsRequestService,
                     private filterService: FilterService) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = ClassifierOptionsRequestsFields.REQUEST_DATE;
    this.linkColumns = [ClassifierOptionsRequestsFields.CLASSIFIER_NAME, ClassifierOptionsRequestsFields.EVALUATION_METHOD];
    this.notSortableColumns = [ClassifierOptionsRequestsFields.CLASSIFIER_NAME];
    this.initColumns();
  }

  public ngOnInit() {
    this.getFilterFields();
  }

  public getFilterFields() {
    this.filterService.getClassifierOptionsRequestFilterFields().subscribe((filterFields: FilterFieldDto[]) => {
      this.filters = this.filterService.mapToFilters(filterFields);
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsRequestDto>> {
    return this.classifierOptionsService.getClassifiersOptionsRequests(pageRequest);
  }

  public getColumnValue(column: string, item: ClassifierOptionsRequestDto) {
    if (column == ClassifierOptionsRequestsFields.CLASSIFIER_NAME) {
      return this.hasClassifierOptionsResponse(item) ? item.classifierOptionsResponseModels[0].classifierName : null;
    } else {
      return super.getColumnValue(column, item);
    }
  }

  public saveClassifierOptions() {
    if (this.selectedRequest) {
      const classifierConfig = this.selectedRequest.classifierOptionsResponseModels[0].options;
      if (classifierConfig) {
        let blob: Blob = new Blob([classifierConfig], {type: 'application/json'});
        saveAs(blob, `${this.selectedRequest.classifierOptionsResponseModels[0].classifierName}_${this.selectedRequest.requestId}.json`);
      }
    }
  }

  public hasClassifierOptionsResponse(item: ClassifierOptionsRequestDto): boolean {
    return item && item.classifierOptionsResponseModels && item.classifierOptionsResponseModels.length > 0;
  }

  public onSelect(event, classifierOptionsRequestDto: ClassifierOptionsRequestDto, column: string, overlayPanel: OverlayPanel) {
    this.selectedRequest = classifierOptionsRequestDto;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  public getFormattedJsonConfig(): string {
    if (this.hasClassifierOptionsResponse(this.selectedRequest)) {
      const classifierConfig = this.selectedRequest.classifierOptionsResponseModels[0].options;
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
      { name: ClassifierOptionsRequestsFields.EVALUATION_METHOD, label: "Метод оценки точности" },
      { name: ClassifierOptionsRequestsFields.REQUEST_DATE, label: "Дата отправки запроса в ERS" },
      { name: ClassifierOptionsRequestsFields.RESPONSE_STATUS, label: "Статус ответа от ERS" }
    ];
  }
}

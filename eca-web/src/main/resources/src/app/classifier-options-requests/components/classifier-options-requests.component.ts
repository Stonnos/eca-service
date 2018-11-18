import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifierOptionsRequestDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService, SelectItem } from "primeng/api";
import { BaseListComponent } from "../../lists/base-list.component";
import { OverlayPanel} from "primeng/primeng";
import { JsonPipe } from "@angular/common";
import { ClassifierOptionsRequestService } from "../services/classifier-options-request.service";
import { Filter } from "../../filter/filter.model";
import { Observable } from "rxjs/internal/Observable";

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
                     private classifierOptionsService: ClassifierOptionsRequestService) {
    super(injector.get(MessageService));
    this.defaultSortField = "requestDate";
    this.linkColumns = ["classifierName", "evaluationMethod"];
    this.notSortableColumns = ["classifierName"];
    this.initColumns();
    this.initFilters();
  }

  public ngOnInit() {
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsRequestDto>> {
    return this.classifierOptionsService.getClassifiersOptionsRequests(pageRequest);
  }

  public getColumnValue(column: string, item: ClassifierOptionsRequestDto) {
    if (column == "classifierName" && this.hasClassifierOptionsResponse(item)) {
      return item.classifierOptionsResponseModels[0].classifierName;
    } else {
      return item[column];
    }
  }

  public hasClassifierOptionsResponse(item: ClassifierOptionsRequestDto): boolean {
    return !!item && !!item.classifierOptionsResponseModels && item.classifierOptionsResponseModels.length > 0;
  }

  public onSelect(event, classifierOptionsRequestDto: ClassifierOptionsRequestDto, column: string, overlayPanel: OverlayPanel) {
    this.selectedRequest = classifierOptionsRequestDto;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  public getFormattedJsonConfig(): string {
    if (this.hasClassifierOptionsResponse(this.selectedRequest)) {
      const classifierConfig = this.selectedRequest.classifierOptionsResponseModels[0].options;
      if (!!classifierConfig) {
        const configObj = JSON.parse(classifierConfig);
        const json = new JsonPipe().transform(configObj);
        return Prism.highlight(json, Prism.languages['json']);
      }
    }
    return null;
  }

  private initColumns() {
    this.columns = [
      { name: "requestId", label: "UUID заявки" },
      { name: "relationName", label: "Обучающая выборка" },
      { name: "classifierName", label: "Классификатор" },
      { name: "evaluationMethod", label: "Метод оценки точности" },
      { name: "requestDate", label: "Дата отправки заявки" },
      { name: "responseStatus", label: "Статус ответа" }
    ];
  }

  private initFilters() {
    const evaluationMethods: SelectItem[] = [
      { label: "Все", value: null },
      { label: "TRAINING_DATA", value: "TRAINING_DATA" },
      { label: "CROSS_VALIDATION", value: "CROSS_VALIDATION" }
    ];
    const statuses: SelectItem[] = [
      { label: "Все", value: null },
      { label: "SUCCESS", value: "SUCCESS" },
      { label: "INVALID_REQUEST_PARAMS", value: "INVALID_REQUEST_PARAMS" },
      { label: "DATA_NOT_FOUND", value: "DATA_NOT_FOUND" },
      { label: "RESULTS_NOT_FOUND", value: "RESULTS_NOT_FOUND" },
      { label: "ERROR", value: "ERROR" }
    ];
    this.filters.push(new Filter("requestId", "UUID заявки",
      "TEXT", "EQUALS", null));
    this.filters.push(new Filter("relationName", "Обучающая выборка",
      "TEXT", "LIKE", null));
    this.filters.push(new Filter("evaluationMethod", "Метод оценки точности", "REFERENCE",
      "EQUALS", null, evaluationMethods));
    this.filters.push(new Filter("responseStatus", "Статус ответа", "REFERENCE",
      "EQUALS", null, statuses));
    this.filters.push(new Filter("requestDate", "Дата отправки заявки с",
      "DATE", "GTE", null));
    this.filters.push(new Filter("requestDate", "Дата отправки заявки по",
      "DATE", "LTE", null));
  }
}

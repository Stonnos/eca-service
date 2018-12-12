import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifierOptionsRequestDto, FilterFieldDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { BaseListComponent } from "../../lists/base-list.component";
import { OverlayPanel} from "primeng/primeng";
import { JsonPipe } from "@angular/common";
import { ClassifierOptionsRequestService } from "../services/classifier-options-request.service";
import { Observable } from "rxjs/internal/Observable";
import { saveAs } from 'file-saver/dist/FileSaver';
import { FilterService } from "../../filter/services/filter.service";

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
    super(injector.get(MessageService));
    this.defaultSortField = "requestDate";
    this.linkColumns = ["classifierName", "evaluationMethod"];
    this.notSortableColumns = ["classifierName"];
    this.initColumns();
    //this.initFilters();
  }

  public ngOnInit() {
    //this.addErsResponsesStatusesFilter();
    this.getFilterFields();
  }

  public getFilterFields() {
    this.filterService.getFilterFields('CLASSIFIER_OPTIONS_REQUEST').subscribe((filterFields: FilterFieldDto[]) => {
      this.filters = this.filterService.mapToFilters(filterFields);
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
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

  public saveClassifierOptions() {
    if (!!this.selectedRequest) {
      const classifierConfig = this.selectedRequest.classifierOptionsResponseModels[0].options;
      if (!!classifierConfig) {
        let blob: Blob = new Blob([classifierConfig], {type: 'application/json'});
        saveAs(blob, `${this.selectedRequest.classifierOptionsResponseModels[0].classifierName}_${this.selectedRequest.requestId}.json`);
      }
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
      { name: "requestDate", label: "Дата отправки запроса" },
      { name: "responseStatus", label: "Статус ответа" }
    ];
  }

  /*private initFilters() {
    const evaluationMethods: SelectItem[] = [
      { label: "Все", value: null },
      { label: "Использование обучающего множества", value: "TRAINING_DATA" },
      { label: "V-блочная кросс-проверка", value: "CROSS_VALIDATION" }
    ];
    this.filters.push(new Filter("requestId", "UUID заявки",
      "TEXT", "LIKE", null));
    this.filters.push(new Filter("relationName", "Обучающая выборка",
      "TEXT", "LIKE", null));
    this.filters.push(new Filter("evaluationMethod", "Метод оценки точности", "REFERENCE",
      "EQUALS", null, evaluationMethods));
    this.filters.push(new Filter("requestDate", "Дата отправки заявки с",
      "DATE", "GTE", null));
    this.filters.push(new Filter("requestDate", "Дата отправки заявки по",
      "DATE", "LTE", null));
  }

  private addErsResponsesStatusesFilter() {
    this.classifierOptionsService.getErsResponsesStatuses().subscribe((enumDtos: EnumDto[]) => {
      const ersResponseStatusesItems: SelectItem[] =
        enumDtos.map((enumDto: EnumDto) => {
          return { label: enumDto.description, value: enumDto.value };
        });
      ersResponseStatusesItems.unshift({ label: "Все", value: null });
      this.filters.push(new Filter("responseStatus", "Статус ответа", "REFERENCE", "EQUALS",
        null, ersResponseStatusesItems));
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }*/
}

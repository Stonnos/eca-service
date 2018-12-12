import { Component, Injector } from '@angular/core';
import {
  EvaluationLogDto, FilterFieldDto,
  PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseListComponent } from "../../lists/base-list.component";
import { MessageService } from "primeng/api";
import { ClassifiersService } from "../services/classifiers.service";
import { OverlayPanel } from "primeng/primeng";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";

@Component({
  selector: 'app-classifier-list',
  templateUrl: './classifier-list.component.html',
  styleUrls: ['./classifier-list.component.scss']
})
export class ClassifierListComponent extends BaseListComponent<EvaluationLogDto> {

  public requestStatusStatisticsDto: RequestStatusStatisticsDto;

  public instancesInfoColumn: string = "instancesInfo.relationName";

  public selectedEvaluationLog: EvaluationLogDto;
  public selectedColumn: string;

  public constructor(private injector: Injector,
                     private classifiersService: ClassifiersService,
                     private filterService: FilterService) {
    super(injector.get(MessageService));
    this.defaultSortField = "creationDate";
    this.linkColumns = ["classifierName", "evaluationMethod", this.instancesInfoColumn];
    this.initColumns();
    //this.initFilters();
  }

  public ngOnInit() {
    //this.getRequestStatusesStatistics();
    this.getFilterFields();
  }

  public getFilterFields() {
    this.filterService.getFilterFields('EVALUATION_LOG').subscribe((filterFields: FilterFieldDto[]) => {
      this.filters = this.filterService.mapToFilters(filterFields);
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<EvaluationLogDto>> {
    return this.classifiersService.getEvaluations(pageRequest);
  }

  public getRequestStatusesStatistics() {
    this.classifiersService.getRequestStatusesStatistics().subscribe((requestStatusStatisticsDto: RequestStatusStatisticsDto) => {
      this.requestStatusStatisticsDto = requestStatusStatisticsDto;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }

  public onSelect(event, evaluationLog: EvaluationLogDto, column: string, overlayPanel: OverlayPanel) {
    this.selectedEvaluationLog = evaluationLog;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  public getColumnValue(column: string, item: EvaluationLogDto) {
    return column == this.instancesInfoColumn ? item.instancesInfo.relationName : item[column];
  }

  private initColumns() {
    this.columns = [
      { name: "requestId", label: "UUID заявки" },
      { name: "classifierName", label: "Классификатор" },
      { name: this.instancesInfoColumn, label: "Обучающая выборка" },
      { name: "evaluationMethod", label: "Метод оценки точности" },
      { name: "creationDate", label: "Дата создания заявки" },
      { name: "startDate", label: "Дата начала построения модели" },
      { name: "endDate", label: "Дата окончания построения модели" },
      { name: "evaluationStatus", label: "Статус заявки" }
    ];
  }

  /*private initFilters() {
    const evaluationMethods: SelectItem[] = [
      { label: "Все", value: null },
      { label: "Использование обучающего множества", value: "TRAINING_DATA" },
      { label: "V-блочная кросс-проверка", value: "CROSS_VALIDATION" }
    ];
    const statuses: SelectItem[] = [
      { label: "Все", value: null },
      { label: "Новая", value: "NEW" },
      { label: "Завершена", value: "FINISHED" },
      { label: "Таймаут", value: "TIMEOUT" },
      { label: "Ошибка", value: "ERROR" }
    ];
    this.filters.push(new Filter("requestId", "UUID заявки",
      "TEXT", "LIKE", null));
    this.filters.push(new Filter("classifierName", "Классификатор",
      "TEXT", "LIKE", null));
    this.filters.push(new Filter("evaluationMethod", "Метод оценки точности", "REFERENCE",
      "EQUALS", null, evaluationMethods));
    this.filters.push(new Filter("evaluationStatus", "Статус заявки", "REFERENCE",
      "EQUALS", null, statuses));
    this.filters.push(new Filter("creationDate", "Дата создания заявки с",
      "DATE", "GTE", null));
    this.filters.push(new Filter("creationDate", "Дата создания заявки по",
      "DATE", "LTE", null));
  }*/
}

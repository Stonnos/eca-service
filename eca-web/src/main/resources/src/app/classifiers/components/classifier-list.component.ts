import { Component, Injector } from '@angular/core';
import {
  EvaluationLogDto,
  PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseListComponent } from "../../lists/base-list.component";
import { MessageService, SelectItem } from "primeng/api";
import { ClassifiersService } from "../services/classifiers.service";
import { Filter } from "../../filter/filter.model";
import { OverlayPanel } from "primeng/primeng";
import { Observable } from "rxjs/internal/Observable";

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
                     private classifiersService: ClassifiersService) {
    super(injector.get(MessageService));
    this.defaultSortField = "creationDate";
    this.linkColumns = ["classifierName", "evaluationMethod", this.instancesInfoColumn];
    this.initColumns();
    this.initFilters();
  }

  public ngOnInit() {
    this.getRequestStatusesStatistics();
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<EvaluationLogDto>> {
    return this.classifiersService.getEvaluations(pageRequest);
  }

  public getRequestStatusesStatistics() {
    this.classifiersService.getRequestStatusesStatistics().subscribe((requestStatusStatisticsDto: RequestStatusStatisticsDto) => {
      this.requestStatusStatisticsDto = requestStatusStatisticsDto;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
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

  private initFilters() {
    const evaluationMethods: SelectItem[] = [
      { label: "Все", value: null },
      { label: "TRAINING_DATA", value: "TRAINING_DATA" },
      { label: "CROSS_VALIDATION", value: "CROSS_VALIDATION" }
    ];
    const statuses: SelectItem[] = [
      { label: "Все", value: null },
      { label: "NEW", value: "NEW" },
      { label: "FINISHED", value: "FINISHED" },
      { label: "TIMEOUT", value: "TIMEOUT" },
      { label: "ERROR", value: "ERROR" }
    ];
    this.filters.push(new Filter("requestId", "UUID заявки",
      "TEXT", "EQUALS", null));
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
  }
}

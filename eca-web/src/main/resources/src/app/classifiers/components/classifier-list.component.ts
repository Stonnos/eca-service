import { Component, Injector } from '@angular/core';
import {
  EvaluationLogDetailsDto,
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
  public evaluationLogDetails: EvaluationLogDetailsDto;
  public evaluationResultsVisibility: boolean = false;

  public instancesInfoColumn: string = "instancesInfo.relationName";
  public requestIdColumn: string = "requestId";

  public selectedEvaluationLog: EvaluationLogDto;
  public selectedColumn: string;

  public constructor(private injector: Injector,
                     private classifiersService: ClassifiersService,
                     private filterService: FilterService) {
    super(injector.get(MessageService));
    this.defaultSortField = "creationDate";
    this.linkColumns = ["classifierName", "evaluationMethod", this.instancesInfoColumn, this.requestIdColumn];
    this.initColumns();
  }

  public ngOnInit() {
    this.getFilterFields();
    this.getRequestStatusesStatistics();
  }

  public getFilterFields() {
    this.filterService.getEvaluationLogFilterFields().subscribe((filterFields: FilterFieldDto[]) => {
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

  public onSelect(event, evaluationLog: EvaluationLogDto, column: string, overlayPanel: OverlayPanel): void {
    if (column == this.requestIdColumn) {
      this.getEvaluationResults(evaluationLog.requestId);
    } else {
      this.selectedEvaluationLog = evaluationLog;
      this.selectedColumn = column;
      overlayPanel.toggle(event);
    }
  }

  public getColumnValue(column: string, item: EvaluationLogDto) {
    return column == this.instancesInfoColumn ? item.instancesInfo.relationName : item[column];
  }

  public onEvaluationResultsVisibilityChange(visible): void {
    this.evaluationResultsVisibility = visible;
  }

  private getEvaluationResults(requestId: string): void {
    this.loading = true;
    this.classifiersService.getEvaluationLogDetailsDto(requestId).subscribe((evaluationLogDetails: EvaluationLogDetailsDto) => {
      this.evaluationLogDetails = evaluationLogDetails;
      this.loading = false;
      this.evaluationResultsVisibility = true;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      this.loading = false;
    });
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
}

import { Component, Injector } from '@angular/core';
import {
  EvaluationLogDto, FilterFieldDto,
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

@Component({
  selector: 'app-classifier-list',
  templateUrl: './classifier-list.component.html',
  styleUrls: ['./classifier-list.component.scss']
})
export class ClassifierListComponent extends BaseListComponent<EvaluationLogDto> {

  private readonly evaluationDetailsUrl: string = '/dashboard/classifiers/evaluation-results';

  public requestStatusStatisticsDto: RequestStatusStatisticsDto;

  public instancesInfoColumn: string = "instancesInfo.relationName";
  public classifierNameColumn: string = "classifierInfo.classifierName";
  public requestIdColumn: string = "requestId";
  public evaluationMethodColumn: string = "evaluationMethod";

  public selectedEvaluationLog: EvaluationLogDto;
  public selectedColumn: string;

  public constructor(private injector: Injector,
                     private classifiersService: ClassifiersService,
                     private filterService: FilterService,
                     private router: Router) {
    super(injector.get(MessageService));
    this.defaultSortField = "creationDate";
    this.linkColumns = [this.classifierNameColumn, this.evaluationMethodColumn, this.instancesInfoColumn, this.requestIdColumn];
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
    switch (column) {
      case this.requestIdColumn:
        this.router.navigate([this.evaluationDetailsUrl, evaluationLog.requestId]);
        break;
      case this.evaluationMethodColumn:
        if (evaluationLog.evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION) {
          this.toggleOverlayPanel(event, evaluationLog, column, overlayPanel);
        }
        break;
      default:
        this.toggleOverlayPanel(event, evaluationLog, column, overlayPanel);
    }
  }

  public getColumnValue(column: string, item: EvaluationLogDto) {
    switch (column) {
      case this.instancesInfoColumn:
        return item.instancesInfo.relationName;
      case this.classifierNameColumn:
        return item.classifierInfo.classifierName;
      case this.evaluationMethodColumn:
        return item.evaluationMethod.description;
      case "evaluationStatus":
        return item.evaluationStatus.description;
      default:
        return item[column];
    }
  }

  private toggleOverlayPanel(event, evaluationLog: EvaluationLogDto, column: string, overlayPanel: OverlayPanel): void {
    this.selectedEvaluationLog = evaluationLog;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  private initColumns() {
    this.columns = [
      { name: "requestId", label: "UUID заявки" },
      { name: this.classifierNameColumn, label: "Классификатор" },
      { name: "evaluationStatus", label: "Статус заявки" },
      { name: this.instancesInfoColumn, label: "Обучающая выборка" },
      { name: this.evaluationMethodColumn, label: "Метод оценки точности" },
      { name: "creationDate", label: "Дата создания заявки" },
      { name: "startDate", label: "Дата начала построения модели" },
      { name: "endDate", label: "Дата окончания построения модели" }
    ];
  }
}

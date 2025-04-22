import { Component, Injector } from '@angular/core';
import {
  EvaluationResultsHistoryDto,
  FilterFieldDto,
  PageDto,
  PageRequestDto,
  RoutePathDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { MessageService } from "primeng/api";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";
import { EvaluationResultsHistoryFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { EvaluationResultsHistoryService } from "../services/evaluation-results-history.service";
import { EvaluationResultsHistoryFilterFields } from "../../common/util/filter-field-names";
import { OverlayPanel } from "primeng/primeng";
import { EvaluationMethod } from "../../common/model/evaluation-method.enum";
import { InstancesInfoFilterValueTransformer } from "../../filter/autocomplete/transformer/instances-info-filter-value-transformer";
import { InstancesInfoAutocompleteHandler } from "../../filter/autocomplete/handler/instances-info-autocomplete-handler";
import { finalize } from 'rxjs/internal/operators';
import { Router } from '@angular/router';

@Component({
  selector: 'app-evaluation-results-history',
  templateUrl: './evaluation-results-history.component.html',
  styleUrls: ['./evaluation-results-history.component.scss']
})
export class EvaluationResultsHistoryComponent extends BaseListComponent<EvaluationResultsHistoryDto> {

  private static readonly EVALUATION_RESULTS_HISTORY_REPORT_FILE_NAME = 'evaluation-results-history-report-template.xlsx';

  public selectedColumn: string;
  public selectedEvaluationResults: EvaluationResultsHistoryDto;

  public constructor(private injector: Injector,
                     private evaluationResultsHistoryService: EvaluationResultsHistoryService,
                     private filterService: FilterService,
                     private router: Router) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = EvaluationResultsHistoryFields.SAVE_DATE;
    this.notSortableColumns = [EvaluationResultsHistoryFields.EVALUATION_METHOD_DESCRIPTION];
    this.linkColumns = [EvaluationResultsHistoryFields.CLASSIFIER_DESCRIPTION, EvaluationResultsHistoryFields.EVALUATION_METHOD_DESCRIPTION,
      EvaluationResultsHistoryFields.RELATION_NAME];
    this.initColumns();
  }

  public ngOnInit() {
    this.addLazyReferenceTransformers(new InstancesInfoFilterValueTransformer());
    this.addAutoCompleteHandler(new InstancesInfoAutocompleteHandler(this.evaluationResultsHistoryService, this.messageService));
    this.getFilterFields();
  }

  public getFilterFields() {
    this.filterService.getEvaluationResultsHistoryFilterFields()
      .subscribe({
        next: (filterFields: FilterFieldDto[]) => {
          this.filters = this.filterService.mapToFilters(filterFields);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<EvaluationResultsHistoryDto>> {
    return this.evaluationResultsHistoryService.getEvaluationResultsHistory(pageRequest);
  }

  public generateReport() {
    const observable = this.evaluationResultsHistoryService.getEvaluationResultsHistoryBaseReport(this.pageRequestDto);
    this.downloadReport(observable, EvaluationResultsHistoryComponent.EVALUATION_RESULTS_HISTORY_REPORT_FILE_NAME);
  }

  public onSelect(event, evaluationResultsHistoryDto: EvaluationResultsHistoryDto, column: string, overlayPanel: OverlayPanel): void {
    if (evaluationResultsHistoryDto.evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION) {
      this.toggleOverlayPanel(event, evaluationResultsHistoryDto, column, overlayPanel);
    } else {
      this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${column} as link`});
    }
  }

  public onEvaluationResultsLink(event, evaluationResultsHistoryDto: EvaluationResultsHistoryDto): void {
    this.loading = true;
    this.evaluationResultsHistoryService.getEvaluationResultsRequestPath(evaluationResultsHistoryDto.requestId)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (routePathDto: RoutePathDto) => {
          this.router.navigate([routePathDto.path]);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private toggleOverlayPanel(event, evaluationResultsHistoryDto: EvaluationResultsHistoryDto, column: string, overlayPanel: OverlayPanel): void {
    this.selectedEvaluationResults = evaluationResultsHistoryDto;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  private initColumns() {
    this.columns = [
      { name: EvaluationResultsHistoryFields.CLASSIFIER_DESCRIPTION, label: "Классификатор", sortBy: EvaluationResultsHistoryFilterFields.CLASSIFIER_NAME },
      { name: EvaluationResultsHistoryFields.RELATION_NAME, label: "Обучающая выборка", sortBy: EvaluationResultsHistoryFilterFields.RELATION_NAME },
      { name: EvaluationResultsHistoryFields.EVALUATION_METHOD_DESCRIPTION, label: "Метод оценки точности" },
      { name: EvaluationResultsHistoryFields.PCT_CORRECT, label: "Точность классификатора, %", sortBy: EvaluationResultsHistoryFilterFields.PCT_CORRECT },
      { name: EvaluationResultsHistoryFields.MEAN_ABSOLUTE_ERROR, label: "Средняя абсолютная ошибка", sortBy: EvaluationResultsHistoryFilterFields.MEAN_ABSOLUTE_ERROR },
      { name: EvaluationResultsHistoryFields.MAX_AUC, label: "Значение max AUC", sortBy: EvaluationResultsHistoryFilterFields.MAX_AUC },
      { name: EvaluationResultsHistoryFields.VARIANCE_ERROR, label: "Дисперсия ошибки", sortBy: EvaluationResultsHistoryFilterFields.VARIANCE_ERROR },
      { name: EvaluationResultsHistoryFields.ROOT_MEAN_SQUARED_ERROR, label: "Среднеквадратическая ошибка", sortBy: EvaluationResultsHistoryFilterFields.ROOT_MEAN_SQUARED_ERROR },
      { name: EvaluationResultsHistoryFields.SAVE_DATE, label: "Дата сохранения", sortBy: EvaluationResultsHistoryFilterFields.SAVE_DATE },
    ];
  }
}

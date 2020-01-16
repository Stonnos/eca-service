import { Component, Injector } from '@angular/core';
import {
  EvaluationLogDto, FilterFieldDto,
  PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { MessageService } from "primeng/api";
import { saveAs } from 'file-saver/dist/FileSaver';
import { ClassifiersService } from "../services/classifiers.service";
import { OverlayPanel } from "primeng/primeng";
import { Observable } from "rxjs/internal/Observable";
import { FilterService } from "../../filter/services/filter.service";
import { EvaluationMethod } from "../../common/model/evaluation-method.enum";
import { Router } from "@angular/router";
import { RouterPaths } from "../../routing/router-paths";
import { EvaluationLogFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { finalize } from "rxjs/operators";
import { ReportsService } from "../../common/services/report.service";
import { Utils} from "../../common/util/utils";

@Component({
  selector: 'app-classifier-list',
  templateUrl: './classifier-list.component.html',
  styleUrls: ['./classifier-list.component.scss']
})
export class ClassifierListComponent extends BaseListComponent<EvaluationLogDto> {

  private static readonly EVALUATION_LOGS_REPORT_FILE_NAME = 'evaluation-logs-report.xlsx';

  public requestStatusStatisticsDto: RequestStatusStatisticsDto;

  public selectedEvaluationLog: EvaluationLogDto;
  public selectedColumn: string;

  public constructor(private injector: Injector,
                     private classifiersService: ClassifiersService,
                     private filterService: FilterService,
                     private reportsService: ReportsService,
                     private router: Router) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = EvaluationLogFields.CREATION_DATE;
    this.linkColumns = [EvaluationLogFields.CLASSIFIER_NAME, EvaluationLogFields.EVALUATION_METHOD,
      EvaluationLogFields.RELATION_NAME, EvaluationLogFields.REQUEST_ID];
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

  public generateReport() {
    this.loading = true;
    this.reportsService.getEvaluationLogsBaseReport(this.pageRequestDto)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((blob: Blob) => {
        saveAs(blob, ClassifierListComponent.EVALUATION_LOGS_REPORT_FILE_NAME);
      }, (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      });
  }

  public getNumFolds(evaluationLog: EvaluationLogDto): number {
    return this.fieldService.getFieldValue(EvaluationLogFields.NUM_FOLDS, evaluationLog, Utils.MISSING_VALUE);
  }

  public getNumTests(evaluationLog: EvaluationLogDto): number {
    return this.fieldService.getFieldValue(EvaluationLogFields.NUM_TESTS, evaluationLog, Utils.MISSING_VALUE);
  }

  public getSeed(evaluationLog: EvaluationLogDto): number {
    return this.fieldService.getFieldValue(EvaluationLogFields.SEED, evaluationLog, Utils.MISSING_VALUE);
  }

  public onSelect(event, evaluationLog: EvaluationLogDto, column: string, overlayPanel: OverlayPanel): void {
    switch (column) {
      case EvaluationLogFields.REQUEST_ID:
        this.router.navigate([RouterPaths.EVALUATION_DETAILS_URL, evaluationLog.requestId]);
        break;
      case EvaluationLogFields.EVALUATION_METHOD:
        if (evaluationLog.evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION) {
          this.toggleOverlayPanel(event, evaluationLog, column, overlayPanel);
        }
        break;
      default:
        this.toggleOverlayPanel(event, evaluationLog, column, overlayPanel);
    }
  }

  private toggleOverlayPanel(event, evaluationLog: EvaluationLogDto, column: string, overlayPanel: OverlayPanel): void {
    this.selectedEvaluationLog = evaluationLog;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  private initColumns() {
    this.columns = [
      { name: EvaluationLogFields.REQUEST_ID, label: "UUID заявки" },
      { name: EvaluationLogFields.CLASSIFIER_NAME, label: "Классификатор" },
      { name: EvaluationLogFields.EVALUATION_STATUS, label: "Статус заявки" },
      { name: EvaluationLogFields.RELATION_NAME, label: "Обучающая выборка" },
      { name: EvaluationLogFields.EVALUATION_METHOD, label: "Метод оценки точности" },
      { name: EvaluationLogFields.CREATION_DATE, label: "Дата создания заявки" },
      { name: EvaluationLogFields.START_DATE, label: "Дата начала построения модели" },
      { name: EvaluationLogFields.END_DATE, label: "Дата окончания построения модели" }
    ];
  }
}

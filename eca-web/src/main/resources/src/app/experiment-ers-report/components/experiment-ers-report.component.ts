import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  ExperimentErsReportDto,
  ExperimentResultsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { finalize } from "rxjs/operators";
import { MessageService } from "primeng/api";
import { Router } from "@angular/router";
import { RouterPaths } from "../../routing/router-paths";
import { FieldLink } from "../../common/model/field-link";
import { FieldService } from "../../common/services/field.service";
import { ExperimentResultsFields } from "../../common/util/field-names";
import { ErsSentStatusEnum } from "../../common/model/ers-sent-status.enum";

@Component({
  selector: 'app-experiment-ers-report',
  templateUrl: './experiment-ers-report.component.html',
  styleUrls: ['./experiment-ers-report.component.scss']
})
export class ExperimentErsReportComponent implements OnInit, FieldLink {

  @Input()
  public experimentErsReport: ExperimentErsReportDto;

  @Output()
  public evaluationResultsSent: EventEmitter<void> = new EventEmitter();

  public linkColumns: string[] = [ExperimentResultsFields.RESULTS_INDEX, ExperimentResultsFields.CLASSIFIER_NAME];
  public experimentResultsColumns: any[] = [];

  public selectedExperimentResults: ExperimentResultsDto;

  public classifierOptionsDialogVisibility: boolean = false;

  public loading: boolean = false;

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService,
                     private router: Router,
                     private fieldService: FieldService) {
    this.initExperimentResultsColumns();
  }

  public ngOnInit(): void {
  }

  public isLink(column: string): boolean {
    return this.linkColumns.includes(column);
  }

  public onLink(experimentResults: ExperimentResultsDto, column: string): void {
    switch (column) {
      case ExperimentResultsFields.RESULTS_INDEX:
        this.router.navigate([RouterPaths.EXPERIMENT_RESULTS_DETAILS_URL, experimentResults.id]);
        break;
      case ExperimentResultsFields.CLASSIFIER_NAME:
        this.selectedExperimentResults = experimentResults;
        this.classifierOptionsDialogVisibility = true;
        break;
      default:
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${column} as link`});
    }
  }

  public hideClassifierOptionsDialog(): void {
    this.classifierOptionsDialogVisibility = false;
  }

  public needSentToErs(): boolean {
    return this.experimentErsReport && this.experimentErsReport.ersReportStatus.value == "NEED_SENT";
  }

  public sentEvaluationResults(): void {
    this.loading = true;
    this.experimentsService.sentEvaluationResults(this.experimentErsReport.experimentUuid)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: data => {
          this.messageService.add({ severity: 'success',
            summary: `Запрос в ERS на сохранение классификаторов для эксперимента ${this.experimentErsReport.experimentUuid} был успешно создан`, detail: '' });
          this.evaluationResultsSent.emit();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getColumnValue(column: string, item: ExperimentResultsDto) {
    if (column == ExperimentResultsFields.SENT) {
      return item.sent ? ErsSentStatusEnum.SENT : ErsSentStatusEnum.NOT_SENT;
    } else {
      return this.fieldService.getFieldValue(column, item);
    }
  }

  private initExperimentResultsColumns() {
    this.experimentResultsColumns = [
      { name: ExperimentResultsFields.RESULTS_INDEX, label: "№" },
      { name: ExperimentResultsFields.CLASSIFIER_NAME, label: "Классификатор" },
      { name: ExperimentResultsFields.PCT_CORRECT, label: "Точность, %" },
      { name: ExperimentResultsFields.SENT, label: "Статус отправки результатов в ERS" }
    ];
  }
}

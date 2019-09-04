import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  ClassifierInfoDto,
  ExperimentErsReportDto, ExperimentResultsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { finalize } from "rxjs/operators";
import { MessageService } from "primeng/api";

@Component({
  selector: 'app-experiment-ers-report',
  templateUrl: './experiment-ers-report.component.html',
  styleUrls: ['./experiment-ers-report.component.scss']
})
export class ExperimentErsReportComponent implements OnInit {

  @Input()
  public experimentErsReport: ExperimentErsReportDto;

  @Output()
  public evaluationResultsSent: EventEmitter<void> = new EventEmitter();

  public linkColumns: string[] = [];
  public experimentResultsColumns: any[] = [];

  public selectedClassifierInfo: ClassifierInfoDto;

  public classifierOptionsDialogVisibility: boolean = false;

  public loading: boolean = false;

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService) {
    this.initExperimentResultsColumns();
    this.linkColumns = ["resultsIndex", "classifierName"];
  }

  public ngOnInit(): void {
  }

  public isLink(column: string): boolean {
    return this.linkColumns.includes(column);
  }

  public onSelect(event, experimentResults: ExperimentResultsDto, column: string): void {
    if (column == "resultsIndex") {
      //TODO
    } else if (column == "classifierName") {
      this.selectedClassifierInfo = experimentResults.classifierInfo;
      this.classifierOptionsDialogVisibility = true;
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
      .subscribe(data => {
        this.messageService.add({ severity: 'success',
          summary: `Запрос в ERS на сохранение классификаторов для эксперимента ${this.experimentErsReport.experimentUuid} был успешно создан`, detail: '' });
        this.evaluationResultsSent.emit();
      }, (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      });
  }

  public getColumnValue(column: string, item: ExperimentResultsDto) {
    switch (column) {
      case "classifierName":
        return item.classifierInfo.classifierName;
      case "sent":
        return item.sent ? "Отправлены" : "Не отправлены";
      default:
        return item[column];
    }
  }

  private initExperimentResultsColumns() {
    this.experimentResultsColumns = [
      { name: "resultsIndex", label: "№" },
      { name: "classifierName", label: "Классификатор" },
      { name: "pctCorrect", label: "Точность, %" },
      { name: "sent", label: "Статус отправки результатов в ERS" }
    ];
  }
}

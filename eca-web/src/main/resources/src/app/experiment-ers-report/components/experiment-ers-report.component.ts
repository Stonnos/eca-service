import { Component, Input, OnInit } from '@angular/core';
import {
  ExperimentErsReportDto, ExperimentResultsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { finalize } from "rxjs/operators";
import { MessageService } from "primeng/api";
import { OverlayPanel } from "primeng/primeng";

@Component({
  selector: 'app-experiment-ers-report',
  templateUrl: './experiment-ers-report.component.html',
  styleUrls: ['./experiment-ers-report.component.scss']
})
export class ExperimentErsReportComponent implements OnInit {

  @Input()
  public experimentErsReport: ExperimentErsReportDto;

  public linkColumns: string[] = [];
  public experimentResultsColumns: any[] = [];

  public selectedExperimentResults: ExperimentResultsDto;

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

  public onSelect(event, experimentResults: ExperimentResultsDto, column: string, overlayPanel: OverlayPanel): void {
    if (column == "resultsIndex") {
      //TODO
    } else {
      this.toggleOverlayPanel(event, experimentResults, overlayPanel);
    }
  }

  public needSentToErs(): boolean {
    return this.experimentErsReport && this.experimentErsReport.ersReportStatus.value == "NEED_SENT";
  }

  public sentEvaluationResults(): void {
   // this.loading = true;
    /*this.experimentsService.sentEvaluationResults(this.experimentUuid)
      .pipe(
        finalize(() => {
        //  this.loading = false;
        })
      )
      .subscribe(data => {
        this.messageService.add({ severity: 'success',
          summary: `Запрос в ERS на сохранение классификаторов для эксперимента ${this.experimentUuid} был успешно создан`, detail: '' });
      }, (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      });*/
  }

  public getColumnValue(column: string, item: ExperimentResultsDto) {
    switch (column) {
      case "classifierName":
        return item.classifierInfo.classifierName;
      case "sent":
        return item.sent ? "Результаты отправлены" : "Результаты не отправлены";
      default:
        return item[column];
    }
  }

  private toggleOverlayPanel(event, experimentResults: ExperimentResultsDto, overlayPanel: OverlayPanel): void {
    this.selectedExperimentResults = experimentResults;
    console.log(this.selectedExperimentResults);
    overlayPanel.toggle(event);
  }

  private initExperimentResultsColumns() {
    this.experimentResultsColumns = [
      { name: "resultsIndex", label: "№" },
      { name: "classifierName", label: "Классификатор" },
      { name: "pctCorrect", label: "Точность, %" },
      { name: "sent", label: "Статус отправки в ERS" }
    ];
  }
}

import { Component, Input, OnInit } from '@angular/core';
import {
  ExperimentErsReportDto, ExperimentProgressDto,
  ExperimentResultsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../../experiments/services/experiments.service";
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
  @Input()
  public experimentProgress: ExperimentProgressDto;

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

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
import { OverlayPanel } from "primeng/overlaypanel";

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

  public linkColumns: string[] = [ExperimentResultsFields.RESULTS_INDEX, ExperimentResultsFields.CLASSIFIER_DESCRIPTION];
  public experimentResultsColumns: any[] = [];

  public selectedExperimentResults: ExperimentResultsDto;

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

  public onLink(event: any, experimentResults: ExperimentResultsDto, column: string, overlayPanel: OverlayPanel): void {
    switch (column) {
      case ExperimentResultsFields.RESULTS_INDEX:
        this.router.navigate([RouterPaths.EXPERIMENT_RESULTS_DETAILS_URL, experimentResults.id]);
        break;
      case ExperimentResultsFields.CLASSIFIER_DESCRIPTION:
        this.selectedExperimentResults = experimentResults;
        overlayPanel.toggle(event);
        break;
      default:
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${column} as link`});
    }
  }

  public getColumnValue(column: string, item: ExperimentResultsDto) {
    return this.fieldService.getFieldValue(column, item);
  }

  private initExperimentResultsColumns() {
    this.experimentResultsColumns = [
      { name: ExperimentResultsFields.RESULTS_INDEX, label: "№" },
      { name: ExperimentResultsFields.CLASSIFIER_DESCRIPTION, label: "Классификатор" },
      { name: ExperimentResultsFields.PCT_CORRECT, label: "Точность, %" }
    ];
  }
}

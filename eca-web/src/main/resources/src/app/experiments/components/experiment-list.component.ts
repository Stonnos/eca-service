import { Component, OnInit } from '@angular/core';
import {
  ExperimentDto, ExperimentStatisticsDto, ExperimentTypeDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../services/experiments.service";
import { MessageService, SelectItem } from "primeng/api";
import { Filter } from "../../filter/filter.model";
import { saveAs } from 'file-saver/dist/FileSaver';
import {BaseListComponent} from "../../lists/base-list.component";

@Component({
  selector: 'app-experiment-list',
  templateUrl: './experiment-list.component.html',
  styleUrls: ['./experiment-list.component.scss']
})
export class ExperimentListComponent extends BaseListComponent<ExperimentDto> implements OnInit {

  private experimentStatistics: ExperimentStatisticsDto;

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService) {
    super();
    this.defaultSortField = "creationDate";
    this.linkColumns = ["trainingDataAbsolutePath", "experimentAbsolutePath"];
    this.initColumns();
    this.initFilters();
  }

  public ngOnInit() {
    this.addExperimentTypesFilter();
    this.getExperimentsStatistics();
  }

  public getNextPage(pageRequest: PageRequestDto) {
    this.experimentsService.getExperiments(pageRequest).subscribe((pageDto: PageDto<ExperimentDto>) => {
      this.items = pageDto.content;
      this.total = pageDto.totalCount;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    });
  }

  public getExperimentsStatistics() {
    this.experimentsService.getExperimentsStatistics().subscribe((experimentStatistics: ExperimentStatisticsDto) => {
      this.experimentStatistics = experimentStatistics;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    });
  }


  public onLink(column: string, experiment: ExperimentDto) {
    switch (column) {
      case this.linkColumns[0]:
        this.experimentsService.getExperimentTrainingDataFile(experiment.uuid).subscribe((blob: Blob) => {
          saveAs(blob, experiment.trainingDataAbsolutePath);
        }, (error) => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
        });
        break;
      case this.linkColumns[1]:
        this.experimentsService.getExperimentResultsFile(experiment.uuid).subscribe((blob: Blob) => {
          saveAs(blob, experiment.experimentAbsolutePath);
        }, (error) => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
        });
        break;
    }
  }

  public getNewExperimentsCount(): number {
    return this.experimentStatistics && this.experimentStatistics.newExperimentsCount;
  }

  public getFinishedExperimentsCount(): number {
    return this.experimentStatistics && this.experimentStatistics.finishedExperimentsCount;
  }

  public getTimeoutExperimentsCount(): number {
    return this.experimentStatistics && this.experimentStatistics.timeoutExperimentsCount;
  }

  public getErrorExperimentsCount(): number {
    return this.experimentStatistics && this.experimentStatistics.errorExperimentsCount;
  }

  public getExperimentsTotalCount(): number {
    return this.experimentStatistics && this.experimentStatistics.totalCount;
  }

  private initColumns() {
    this.columns = [
      { name: "uuid", label: "Request UUID" },
      { name: "experimentType", label: "Experiment type" },
      { name: "evaluationMethod", label: "Evaluation method" },
      { name: "firstName", label: "Request first name" },
      { name: "email", label: "Request email" },
      { name: "trainingDataAbsolutePath", label: "Training data" },
      { name: "experimentAbsolutePath", label: "Experiment results" },
      { name: "creationDate", label: "Creation date" },
      { name: "startDate", label: "Start date" },
      { name: "endDate", label: "End date" },
      { name: "sentDate", label: "Sent date" },
      { name: "deletedDate", label: "Delete date" },
      { name: "experimentStatus", label: "Status" }
    ];
  }

  private initFilters() {
    const evaluationMethods: SelectItem[] = [
      { label: "TRAINING_DATA", value: "TRAINING_DATA" },
      { label: "CROSS_VALIDATION", value: "CROSS_VALIDATION" }
    ];
    const statuses: SelectItem[] = [
      { label: "NEW", value: "NEW" },
      { label: "FINISHED", value: "FINISHED" },
      { label: "TIMEOUT", value: "TIMEOUT" },
      { label: "ERROR", value: "ERROR" }
    ];
    this.filters.push(new Filter("uuid", "Request UUID",
      "TEXT", "EQUALS", null));
    this.filters.push(new Filter("evaluationMethod", "Evaluation method", "REFERENCE",
      "EQUALS", null, evaluationMethods));
    this.filters.push(new Filter("experimentStatus", "Experiment status", "REFERENCE",
      "EQUALS", null, statuses));
    this.filters.push(new Filter("creationDate", "Creation date from",
      "DATE", "GTE", null));
    this.filters.push(new Filter("creationDate", "Creation date to",
      "DATE", "LTE", null));
  }

  private addExperimentTypesFilter() {
    this.experimentsService.getExperimentTypes().subscribe((experimentTypes: ExperimentTypeDto[]) => {
      const experimentTypeItems: SelectItem[] =
        experimentTypes.map((experimentType: ExperimentTypeDto) => {
          return { label: experimentType.description, value: experimentType.type };
        });
      this.filters.push(new Filter("experimentType", "Experiment type", "REFERENCE", "EQUALS",
        null, experimentTypeItems));
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    });
  }
}

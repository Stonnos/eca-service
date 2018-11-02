import { Component, OnInit, ViewChild } from '@angular/core';
import {
  ExperimentDto, ExperimentStatisticsDto, ExperimentTypeDto, FilterRequestDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../services/experiments.service";
import { LazyLoadEvent, MessageService, SelectItem } from "primeng/api";
import { Filter } from "../../filter/filter.model";
import { DatePipe } from "@angular/common";
import { saveAs } from 'file-saver/dist/FileSaver';
import { Table } from "primeng/table";

@Component({
  selector: 'app-experiment-list',
  templateUrl: './experiment-list.component.html',
  styleUrls: ['./experiment-list.component.scss']
})
export class ExperimentListComponent implements OnInit {

  public columns: any[] = [];
  public experiments: ExperimentDto[] = [];
  public experimentStatistics: ExperimentStatisticsDto;

  public total: number = 0;
  public pageSize: number = 25;

  public defaultSortField: string = "creationDate";
  public filters: Filter[] = [];

  private dateFormat: string = "yyyy-MM-dd HH:mm:ss";

  private linkColumns: string[] = ["trainingDataAbsolutePath", "experimentAbsolutePath"];

  @ViewChild(Table)
  private experimentTable: Table;

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService) {
  }

  public ngOnInit() {
    this.initColumns();
    this.initFilters();
    this.getExperimentsStatistics();
  }

  public getExperiments(pageRequest: PageRequestDto) {
    this.experimentsService.getExperiments(pageRequest).subscribe((pageDto: PageDto<ExperimentDto>) => {
      this.experiments = pageDto.content;
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

  public onLazyLoad(event: LazyLoadEvent) {
    const page: number = Math.round(event.first / event.rows);
    const pageRequest: PageRequestDto = {
      page: page,
      size: event.rows,
      sortField: event.sortField,
      ascending: event.sortOrder == 1,
      filters: this.buildFilters()
    };
    this.getExperiments(pageRequest);
  }

  public onSearch() {
    this.resetSort();
    const pageRequest: PageRequestDto = {
      page: 0,
      size: this.pageSize,
      sortField: this.defaultSortField,
      ascending: false,
      filters: this.buildFilters()
    };
    this.getExperiments(pageRequest);
  }

  public onLink(column: string, experiment: ExperimentDto) {
    switch (column) {
      case this.linkColumns[0]:
        console.log("Training data");
        break;
      case this.linkColumns[1]:
        this.experimentsService.getExperimentResultsFile(experiment.uuid).subscribe((blob: Blob) => {
          saveAs(blob, "experiment.model");
        }, (error) => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
        });
        break;
    }
  }

  public isLink(column: string): boolean {
    return this.linkColumns.includes(column);
  }

  public getNewExperimentsCount(): number {
    return this.experimentStatistics && this.experimentStatistics.totalCount;
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

  private buildFilters(): FilterRequestDto[] {
    return this.filters.filter((filter: Filter) => !!filter.currentValue).map((filter: Filter) => {
      return { name: filter.name, value: this.transformFilterValue(filter), filterType: filter.type, matchMode: filter.matchMode };
    });
  }

  private transformFilterValue(filter: Filter): string {
    switch (filter.type) {
      case "DATE":
        return new DatePipe("en-US").transform(filter.currentValue, this.dateFormat);
      case "REFERENCE":
        return filter.currentValue.value;
      default:
        return filter.currentValue;
    }
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

    this.addExperimentTypesFilter();
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

  private resetSort() {
    this.experimentTable.sortField = this.defaultSortField;
    this.experimentTable.sortOrder = -1;
  }
}

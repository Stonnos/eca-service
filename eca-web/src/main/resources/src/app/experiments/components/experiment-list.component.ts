import { Component, OnInit } from '@angular/core';
import {
  ExperimentDto, ExperimentPageDto, FilterRequestDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../services/experiments.service";
import { LazyLoadEvent, MessageService, SelectItem } from "primeng/api";
import { Filter } from "../../filter/filter.model";
import { DatePipe } from "@angular/common";

@Component({
  selector: 'app-experiment-list',
  templateUrl: './experiment-list.component.html',
  styleUrls: ['./experiment-list.component.scss']
})
export class ExperimentListComponent implements OnInit {

  public columns: any[] = [];
  public experiments: ExperimentDto[] = [];

  public total: number = 0;
  public newExperimentsCount: number = 0;
  public finishedExperimentsCount: number = 0;
  public timeoutExperimentsCount: number = 0;
  public errorExperimentsCount: number = 0;
  public pageSize: number = 25;

  public filters: Filter[] = [];

  private defaultSortField: string = "creationDate";
  private dateFormat: string = "yyyy-MM-dd HH:mm:ss";

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService) {
  }

  public ngOnInit() {
    this.initColumns();
    this.initFilters();
  }

  public getExperiments(pageRequest: PageRequestDto) {
    this.experimentsService.getExperiments(pageRequest).subscribe((pageDto: ExperimentPageDto) => {
      this.experiments = pageDto.content;
      this.total = pageDto.totalCount;
      this.newExperimentsCount = pageDto.newExperimentsCount;
      this.finishedExperimentsCount = pageDto.finishedExperimentsCount;
      this.timeoutExperimentsCount = pageDto.timeoutExperimentsCount;
      this.errorExperimentsCount = pageDto.errorExperimentsCount;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    })
  }

  public onLazyLoad(event: LazyLoadEvent) {
    const sortField = !!event.sortField ? event.sortField : this.defaultSortField;
    const ascending = !!event.sortField && !!event.sortOrder && event.sortOrder == 1;
    const page: number = Math.round(event.first / event.rows);
    const pageRequest: PageRequestDto = {
      page: page,
      size: event.rows,
      sortField: sortField,
      ascending: ascending,
      filters: this.buildFilters()
    };
    this.getExperiments(pageRequest);
  }

  public onSearch() {
    const pageRequest: PageRequestDto = {
      page: 0,
      size: this.pageSize,
      sortField: this.defaultSortField,
      ascending: false,
      filters: this.buildFilters()
    };
    this.getExperiments(pageRequest);
  }

  private buildFilters(): FilterRequestDto[] {
    return this.filters.map((filter: Filter) => {
      return { name: filter.name, value: this.transformFilterValue(filter), filterType: filter.type, matchMode: filter.matchMode };
    });
  }

  private transformFilterValue(filter: Filter): string {
    switch (filter.type) {
      case "DATE":
        return filter.currentValue && new DatePipe("en-US").transform(filter.currentValue, this.dateFormat);
      case "REFERENCE":
        return filter.currentValue && filter.currentValue.value;
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
    this.filters.push(new Filter("experimentType", "Experiment type", "REFERENCE", "EQUALS",
      null, [{ label: "KNN", value: "KNN" }]));
    this.filters.push(new Filter("evaluationMethod", "Evaluation method", "REFERENCE",
      "EQUALS", null, evaluationMethods));
    this.filters.push(new Filter("experimentStatus", "Experiment status", "REFERENCE",
      "EQUALS", null, statuses));
    this.filters.push(new Filter("creationDate", "Creation date from",
      "DATE", "GTE", null));
    this.filters.push(new Filter("creationDate", "Creation date to",
      "DATE", "LTE", null));
  }
}

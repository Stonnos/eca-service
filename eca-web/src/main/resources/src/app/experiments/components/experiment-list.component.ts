import { Component, OnInit } from '@angular/core';
import {
  ExperimentDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../services/experiments.service";
import { LazyLoadEvent, MessageService } from "primeng/api";
import { Filter } from "../../filter/filter.model";

@Component({
  selector: 'app-experiment-list',
  templateUrl: './experiment-list.component.html',
  styleUrls: ['./experiment-list.component.scss']
})
export class ExperimentListComponent implements OnInit {

  public columns: any[] = [];
  public experiments: ExperimentDto[] = [];

  public total: number = 0;
  public pageSize: number = 25;

  public filters: Filter[] = [];

  private defaultSortField: string = "creationDate";

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService) {
  }

  public ngOnInit() {
    this.initColumns();
    this.initFilters();
  }

  public getExperiments(pageRequest: PageRequestDto) {
    this.experimentsService.getExperiments(pageRequest).subscribe((pageDto: PageDto<ExperimentDto>) => {
      this.experiments = pageDto.content;
      this.total = pageDto.totalCount;
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
      filters: this.filters.map((filter: Filter) => {
        return { name: filter.name, value: filter.currentValue, filterType: filter.type, matchMode: filter.matchMode };
      })
    };
    this.getExperiments(pageRequest);
  }

  public onSearch() {
    console.log('Search!!');
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
    /*this.filters.push({ name: "experimentType", value: null, matchMode: "EQUALS" });
    this.filters.push({ name: "evaluationMethod", value: null, matchMode: "EQUALS" });
    this.filters.push({ name: "experimentStatus", value: null, matchMode: "EQUALS" });
    this.filters.push({ name: "creationDate", value: null, matchMode: "GTE" });
    this.filters.push({ name: "creationDate", value: null, matchMode: "LTE" });*/
    this.filters.push(new Filter("uuid", "Request UUID",
      "TEXT", "EQUALS", null));
    this.filters.push(new Filter("experimentType", "Experiment type", "REFERENCE", "EQUALS",
      null, [{label: "KNN", value: "KNN"}, {label: "KNN1", value: "KNN1"}]));
    this.filters.push(new Filter("evaluationMethod", "Evaluation method", "REFERENCE",
      "EQUALS", null, [{label: "KNN2", value: "KNN3"}, {label: "KNN13", value: "KNN14"}]));
    this.filters.push(new Filter("experimentStatus", "Experiment status", "REFERENCE",
      "EQUALS", null, [{label: "KNN54", value: "KNN65"}, {label: "KNN17", value: "KNN17"}]));
    this.filters.push(new Filter("creationDate", "Creation date from",
      "DATE", "GTE", null));
    this.filters.push(new Filter("creationDate", "Creation date to",
      "DATE", "LTE", null));
  }
}

import { Component, OnInit } from '@angular/core';
import { ExperimentDto, PageDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../services/experiments.service";
import { LazyLoadEvent, MessageService } from "primeng/api";

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

  public defaultSortField: string = "creationDate";

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService) {
  }

  public ngOnInit() {
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
    this.getExperiments(0, this.pageSize, this.defaultSortField, false);
  }

  public getExperiments(offset: number, size: number, sortField: string, ascending: boolean) {
    const page: number = Math.round(offset / size);
    this.experimentsService.getExperiments(page, size, sortField, ascending).subscribe((pageDto: PageDto<ExperimentDto>) => {
      this.experiments = pageDto.content;
      this.total = pageDto.totalCount;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    })
  }

  public onLazyLoad(event: LazyLoadEvent) {
    const sortField = !!event.sortField ? event.sortField : this.defaultSortField;
    const ascending = !!event.sortField && !!event.sortOrder && event.sortOrder == 1;
    this.getExperiments(event.first, event.rows, sortField, ascending);
  }
}

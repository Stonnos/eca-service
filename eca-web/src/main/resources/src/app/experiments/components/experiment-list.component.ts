import { Component, Injector, OnInit } from '@angular/core';
import {
  ExperimentDto, ExperimentTypeDto, PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../services/experiments.service";
import { MessageService, SelectItem } from "primeng/api";
import { Filter } from "../../filter/filter.model";
import { saveAs } from 'file-saver/dist/FileSaver';
import { BaseListComponent } from "../../lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";

@Component({
  selector: 'app-experiment-list',
  templateUrl: './experiment-list.component.html',
  styleUrls: ['./experiment-list.component.scss']
})
export class ExperimentListComponent extends BaseListComponent<ExperimentDto> implements OnInit {

  public requestStatusStatisticsDto: RequestStatusStatisticsDto;

  public constructor(private injector: Injector,
                     private experimentsService: ExperimentsService) {
    super(injector.get(MessageService));
    this.defaultSortField = "creationDate";
    this.linkColumns = ["trainingDataAbsolutePath", "experimentAbsolutePath"];
    this.initColumns();
    this.initFilters();
  }

  public ngOnInit() {
    this.addExperimentTypesFilter();
    this.getRequestStatusesStatistics();
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ExperimentDto>> {
    return this.experimentsService.getExperiments(pageRequest);
  }

  public getRequestStatusesStatistics() {
    this.experimentsService.getRequestStatusesStatistics().subscribe((requestStatusStatisticsDto: RequestStatusStatisticsDto) => {
      this.requestStatusStatisticsDto = requestStatusStatisticsDto;
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
      { label: "All", value: null },
      { label: "TRAINING_DATA", value: "TRAINING_DATA" },
      { label: "CROSS_VALIDATION", value: "CROSS_VALIDATION" }
    ];
    const statuses: SelectItem[] = [
      { label: "All", value: null },
      { label: "NEW", value: "NEW" },
      { label: "FINISHED", value: "FINISHED" },
      { label: "TIMEOUT", value: "TIMEOUT" },
      { label: "ERROR", value: "ERROR" }
    ];
    this.filters.push(new Filter("uuid", "Request UUID",
      "TEXT", "EQUALS", null));
    this.filters.push(new Filter("email", "Request email",
      "TEXT", "LIKE", null));
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
      experimentTypeItems.unshift({ label: "All", value: null });
      this.filters.push(new Filter("experimentType", "Experiment type", "REFERENCE", "EQUALS",
        null, experimentTypeItems));
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    });
  }
}

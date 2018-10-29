import { Component, OnInit } from '@angular/core';
import { ExperimentDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ExperimentsService } from "../services/experiments.service";
import { MessageService } from "primeng/api";

@Component({
  selector: 'app-experiment-list',
  templateUrl: './experiment-list.component.html',
  styleUrls: ['./experiment-list.component.scss']
})
export class ExperimentListComponent implements OnInit {

  public experiments: ExperimentDto[] = [];

  public first: number = 0;
  public total: number = 0;

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService) {
  }

  public ngOnInit() {
    this.getExperiments();
  }

  public getExperiments() {
    this.experimentsService.getExperiments().subscribe((experiments: ExperimentDto[]) => {
      console.log(experiments.length);
      this.experiments = experiments;
      this.first = 0;
      this.total = this.experiments.length;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    })
  }
}

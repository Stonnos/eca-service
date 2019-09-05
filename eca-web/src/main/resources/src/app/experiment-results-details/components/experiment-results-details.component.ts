import { Component, OnInit } from '@angular/core';
import {
  ExperimentDto, ExperimentResultsDetailsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { ActivatedRoute, Router } from "@angular/router";
import { finalize } from "rxjs/internal/operators";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { RouterPaths } from "../../routing/router.paths";

@Component({
  selector: 'app-experiment-results-details',
  templateUrl: './experiment-results-details.component.html',
  styleUrls: ['./experiment-results-details.component.scss']
})
export class ExperimentResultsDetailsComponent implements OnInit {

  private readonly evaluationResultsId: number;

  public experimentFields: any[] = [];
  public loading: boolean = false;

  public experimentResultsDetailsDto: ExperimentResultsDetailsDto;

  public linkColumns: string[] = ["uuid"];

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService,
                     private route: ActivatedRoute,
                     private router: Router) {
    this.evaluationResultsId = this.route.snapshot.params.id;
    this.initExperimentFields();
  }

  public ngOnInit(): void {
    this.getExperimentResultsDetails();
  }

  public getExperimentResultsDetails(): void {
    this.loading = true;
    this.experimentsService.getExperimentResultsDetails(this.evaluationResultsId)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((experimentResultsDetailsDto: ExperimentResultsDetailsDto) => {
        this.experimentResultsDetailsDto = experimentResultsDetailsDto;
      }, (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      });
  }

  public getExperimentValue(field: string) {
    if (this.experimentResultsDetailsDto && this.experimentResultsDetailsDto.experimentDto) {
      const experimentDto: ExperimentDto = this.experimentResultsDetailsDto.experimentDto;
      switch (field) {
        case "experimentType":
          return experimentDto.experimentType.description;
        case "evaluationMethod":
          return experimentDto.evaluationMethod.description;
        default:
          return experimentDto[field];
      }
    }
    return null;
  }

  public isLink(field: string): boolean {
    return this.linkColumns.includes(field);
  }

  public onLink(field: string): void {
    if (field == "uuid") {
      this.router.navigate([RouterPaths.EXPERIMENT_DETAILS_URL, this.experimentResultsDetailsDto.experimentDto.uuid]);
    } else {
      this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${field} as link`});
    }
  }

  private initExperimentFields(): void {
    this.experimentFields = [
      { name: "uuid", label: "UUID заявки" },
      { name: "experimentType", label: "Тип эксперимента" },
      { name: "evaluationMethod", label: "Метод оценки точности" }
    ];
  }
}

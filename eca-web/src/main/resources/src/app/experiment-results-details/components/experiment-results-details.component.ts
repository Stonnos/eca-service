import { Component, OnInit } from '@angular/core';
import {
  ExperimentDto, ExperimentResultsDetailsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { ActivatedRoute, Router } from "@angular/router";
import { finalize } from "rxjs/internal/operators";
import { ExperimentsService } from "../../experiments/services/experiments.service";
import { RouterPaths } from "../../routing/router-paths";
import { ExperimentFields } from "../../common/util/field-names";
import { FieldLink } from "../../common/model/field-link";
import { FieldService } from "../../common/services/field.service";

@Component({
  selector: 'app-experiment-results-details',
  templateUrl: './experiment-results-details.component.html',
  styleUrls: ['./experiment-results-details.component.scss']
})
export class ExperimentResultsDetailsComponent implements OnInit, FieldLink {

  private readonly evaluationResultsId: number;

  public experimentFields: any[] = [];
  public loading: boolean = false;

  public experimentResultsDetailsDto: ExperimentResultsDetailsDto;

  public linkColumns: string[] = [ExperimentFields.UUID];

  public constructor(private experimentsService: ExperimentsService,
                     private messageService: MessageService,
                     private route: ActivatedRoute,
                     private router: Router,
                     private fieldService: FieldService) {
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
    return this.fieldService.getFieldValue(field, this.experimentResultsDetailsDto && this.experimentResultsDetailsDto.experimentDto);
  }

  public isLink(field: string): boolean {
    return this.linkColumns.includes(field);
  }

  public onLink(field: string): void {
    if (field == ExperimentFields.UUID) {
      this.router.navigate([RouterPaths.EXPERIMENT_DETAILS_URL, this.experimentResultsDetailsDto.experimentDto.uuid]);
    } else {
      this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${field} as link`});
    }
  }

  private initExperimentFields(): void {
    this.experimentFields = [
      { name: ExperimentFields.UUID, label: "UUID заявки" },
      { name: ExperimentFields.EXPERIMENT_TYPE, label: "Тип эксперимента" },
      { name: ExperimentFields.EVALUATION_METHOD, label: "Метод оценки точности" }
    ];
  }
}

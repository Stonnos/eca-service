import { Component, OnInit } from '@angular/core';
import {
  EnumDto,
  EvaluationLogDetailsDto, EvaluationResultsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { EvaluationResultsStatusEnum } from "../model/evaluation-results-status.enum";
import { EvaluationMethod } from "../../model/evaluation-method.enum";
import { ClassifiersService } from "../../classifiers/services/classifiers.service";
import { MessageService } from "primeng/api";
import { ActivatedRoute } from "@angular/router";
import { finalize } from "rxjs/internal/operators";

@Component({
  selector: 'app-evaluation-results',
  templateUrl: './evaluation-results.component.html',
  styleUrls: ['./evaluation-results.component.scss']
})
export class EvaluationResultsComponent implements OnInit {

  private readonly requestId: string;

  public loading: boolean = false;

  public evaluationLogDetails: EvaluationLogDetailsDto;

  public constructor(private classifiersService: ClassifiersService,
                     private messageService: MessageService,
                     private route: ActivatedRoute) {
    this.requestId = this.route.snapshot.params.id;
  }

  public ngOnInit(): void {
    this.getEvaluationResults(this.requestId);
  }

  public getEvaluationResults(requestId: string): void {
    this.loading = true;
    this.classifiersService.getEvaluationLogDetailsDto(requestId)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((evaluationLogDetails: EvaluationLogDetailsDto) => {
        this.evaluationLogDetails = evaluationLogDetails;
      }, (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      });
  }

  public isEvaluationResultsReceived(): boolean {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsStatus.value == EvaluationResultsStatusEnum.RESULTS_RECEIVED;
  }

  public isEvaluationInProgress(): boolean {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsStatus.value == EvaluationResultsStatusEnum.EVALUATION_IN_PROGRESS;
  }

  public isEvaluationResultsErrorStatus(): boolean {
    return !this.isEvaluationInProgress() && !this.isEvaluationResultsReceived();
  }

  public isCrossValidationMethod(): boolean {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION;
  }

  public getEvaluationMethod(): string {
    if (!!this.evaluationLogDetails) {
      const evaluationMethod: EnumDto = this.evaluationLogDetails.evaluationMethod;
      if (evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION) {
        if (this.evaluationLogDetails.numTests == 1) {
          return `${this.evaluationLogDetails.evaluationMethod.description} (${this.evaluationLogDetails.numFolds} блочная)`;
        } else {
          return `${this.evaluationLogDetails.evaluationMethod.description} (${this.evaluationLogDetails.numTests}×${this.evaluationLogDetails.numFolds} блочная)`;
        }
      } else {
        return this.evaluationLogDetails.evaluationMethod.description;
      }
    }
    return null;
  }

  public getConfidenceInterval(): string {
    if (!!this.evaluationLogDetails) {
      const evaluationResults: EvaluationResultsDto = this.evaluationLogDetails.evaluationResultsDto;
      return `[${evaluationResults.confidenceIntervalLowerBound}; ${evaluationResults.confidenceIntervalUpperBound}]`;
    }
    return null;
  }
}

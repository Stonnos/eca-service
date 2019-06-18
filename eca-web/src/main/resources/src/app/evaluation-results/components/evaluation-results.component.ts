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

  public metaInfoRows: any[] = [];
  public evaluationResultsRows: any[] = [];
  public loading: boolean = false;

  public evaluationLogDetails: EvaluationLogDetailsDto;

  public constructor(private classifiersService: ClassifiersService,
                     private messageService: MessageService,
                     private route: ActivatedRoute) {
    this.requestId = this.route.snapshot.params.id;
    this.initMetaInfoRows();
    this.initEvaluationResultsRows();
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

  public getConfidenceInterval(): string {
    const evaluationResults: EvaluationResultsDto = this.evaluationLogDetails.evaluationResultsDto;
    return `[${evaluationResults.confidenceIntervalLowerBound}; ${evaluationResults.confidenceIntervalUpperBound}]`;
  }

  public getMetaInfoRowValue(rowName: string) {
    switch (rowName) {
      case "evaluationMethod":
        return this.getEvaluationMethod();
      case "evaluationStatus":
        return this.evaluationLogDetails.evaluationStatus.description;
      default:
        const tokens: string[] = rowName.split(".");
        return tokens.length == 2 ? this.evaluationLogDetails[tokens[0]][tokens[1]] : this.evaluationLogDetails[tokens[0]];
    }
  }

  public getEvaluationResultsRowValue(rowName: string) {
    const evaluationResults: EvaluationResultsDto = this.evaluationLogDetails.evaluationResultsDto;
    switch (rowName) {
      case "confidenceInterval":
        return this.getConfidenceInterval();
      default:
        return evaluationResults[rowName];
    }
  }

  private initMetaInfoRows(): void {
    this.metaInfoRows = [
      { name: "requestId", label: "UUID заявки:" },
      { name: "classifierName", label: "Классификатор:" },
      { name: "evaluationStatus", label: "Статус заявки:" },
      { name: "instancesInfo.relationName", label: "Обучающая выборка:" },
      { name: "instancesInfo.numInstances", label: "Число объектов:" },
      { name: "instancesInfo.numAttributes", label: "Число атрибутов:" },
      { name: "instancesInfo.numClasses", label: "Число классов:" },
      { name: "instancesInfo.className", label: "Атрибут класса:" },
      { name: "evaluationMethod", label: "Метод оценки точности:" }
    ];
  }

  private initEvaluationResultsRows(): void {
    this.evaluationResultsRows = [
      { name: "numTestInstances", label: "Число объектов тестовых данных:" },
      { name: "numCorrect", label: "Число правильно классифицированных объектов:" },
      { name: "numIncorrect", label: "Число неправильно классифицированных объектов:" },
      { name: "pctCorrect", label: "Точность классификатора, %:" },
      { name: "pctIncorrect", label: "Ошибка классификатора, %:" },
      { name: "meanAbsoluteError", label: "Средняя абсолютная ошибка классификатора:" },
      { name: "rootMeanSquaredError", label: "Среднеквадратическая ошибка классификатора:" },
      { name: "varianceError", label: "Дисперсия ошибки классификатора:" },
      { name: "confidenceInterval", label: "95% доверительный интервал ошибки классификатора:" },
    ];
  }
}

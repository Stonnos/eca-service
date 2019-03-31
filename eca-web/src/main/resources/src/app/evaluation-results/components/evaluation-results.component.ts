import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import {
  EnumDto,
  EvaluationLogDetailsDto, EvaluationResultsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { EvaluationResultsStatusEnum } from "../model/evaluation-results-status.enum";
import { EvaluationMethod } from "../../model/evaluation-method.enum";
import { Fieldset } from "primeng/primeng";

@Component({
  selector: 'app-evaluation-results',
  templateUrl: './evaluation-results.component.html',
  styleUrls: ['./evaluation-results.component.scss']
})
export class EvaluationResultsComponent implements OnInit {

  private evaluationResultsStatusErrorMessageMap = new Map<string, string>()
    .set(EvaluationResultsStatusEnum.EVALUATION_ERROR, 'Невозможно получить результаты классификации, т.к. произошла ошибка при построении модели классификатора')
    .set(EvaluationResultsStatusEnum.ERROR, 'Не удалось получить результаты классификации, т.к. произошла неизвестная ошибка')
    .set(EvaluationResultsStatusEnum.ERS_SERVICE_UNAVAILABLE, 'Не удалось получить результаты классификации, т.к. ERS сервис не доступен')
    .set(EvaluationResultsStatusEnum.EVALUATION_RESULTS_NOT_FOUND, 'Результаты классификации не были найдены в ERS')
    .set(EvaluationResultsStatusEnum.RESULTS_NOT_SENT, 'Не удалось получить результаты классификации, т.к. они не были отправлены в ERS сервис');

  @ViewChild('inputOptions')
  private inputOptionsFieldSet: Fieldset;

  @Input()
  public visible: boolean = false;

  @Input()
  public evaluationLogDetails: EvaluationLogDetailsDto;

  @Output()
  public visibilityChange: EventEmitter<boolean> = new EventEmitter();

  public ngOnInit(): void {
  }

  public isEvaluationResultsReceived(): boolean {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsStatus == EvaluationResultsStatusEnum.RESULTS_RECEIVED;
  }

  public isEvaluationInProgress(): boolean {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsStatus == EvaluationResultsStatusEnum.EVALUATION_IN_PROGRESS;
  }

  public isEvaluationResultsErrorStatus(): boolean {
    return !this.isEvaluationInProgress() && !this.isEvaluationResultsReceived();
  }

  public isCrossValidationMethod(): boolean {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION;
  }

  public getEvaluationResultsStatusErrorMessage(): string {
    return this.evaluationLogDetails && this.evaluationResultsStatusErrorMessageMap.get(this.evaluationLogDetails.evaluationResultsStatus);
  }

  public getRequestId(): string {
    return this.evaluationLogDetails && this.evaluationLogDetails.requestId;
  }

  public getClassifierName(): string {
    return this.evaluationLogDetails && this.evaluationLogDetails.classifierName;
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

  public getInstancesName(): string {
    return this.evaluationLogDetails && this.evaluationLogDetails.instancesInfo.relationName;
  }

  public getNumInstances(): number {
    return this.evaluationLogDetails && this.evaluationLogDetails.instancesInfo.numInstances;
  }

  public getNumAttributes(): number {
    return this.evaluationLogDetails && this.evaluationLogDetails.instancesInfo.numAttributes;
  }

  public getNumClasses(): number {
    return this.evaluationLogDetails && this.evaluationLogDetails.instancesInfo.numClasses;
  }

  public getClassName(): string {
    return this.evaluationLogDetails && this.evaluationLogDetails.instancesInfo.className;
  }

  public getNumTestInstances(): number {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsDto.numTestInstances;
  }

  public getNumCorrect(): number {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsDto.numCorrect;
  }

  public getNumInCorrect(): number {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsDto.numIncorrect;
  }

  public getPctCorrect(): number {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsDto.pctCorrect;
  }

  public getPctIncorrect(): number {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsDto.pctIncorrect;
  }

  public getMeanAbsoluteError(): number {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsDto.meanAbsoluteError;
  }

  public getRootMeanSquaredError(): number {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsDto.rootMeanSquaredError;
  }

  public getVarianceError(): number {
    return this.evaluationLogDetails && this.evaluationLogDetails.evaluationResultsDto.varianceError;
  }

  public getConfidenceInterval(): string {
    if (!!this.evaluationLogDetails) {
      const evaluationResults: EvaluationResultsDto = this.evaluationLogDetails.evaluationResultsDto;
      return `[${evaluationResults.confidenceIntervalLowerBound}; ${evaluationResults.confidenceIntervalUpperBound}]`;
    }
    return null;
  }

  public hide(): void {
    this.inputOptionsFieldSet.collapsed = true;
    this.visibilityChange.emit(false);
  }
}

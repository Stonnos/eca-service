import { Component, Input, OnInit } from '@angular/core';
import {
  EvaluationResultsDto, EvaluationStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { EvaluationResultsStatusEnum } from "../model/evaluation-results-status.enum";

@Component({
  selector: 'app-evaluation-results',
  templateUrl: './evaluation-results.component.html',
  styleUrls: ['./evaluation-results.component.scss']
})
export class EvaluationResultsComponent implements OnInit {

  @Input()
  public evaluationResults: EvaluationResultsDto;

  public evaluationStatisticsRows: any[] = [];

  public constructor() {
    this.initEvaluationStatisticsRows();
  }

  public ngOnInit(): void {
  }

  public isEvaluationResultsReceived(): boolean {
    return this.evaluationResults && this.evaluationResults.evaluationResultsStatus.value == EvaluationResultsStatusEnum.RESULTS_RECEIVED;
  }

  public isEvaluationInProgress(): boolean {
    return this.evaluationResults && this.evaluationResults.evaluationResultsStatus.value == EvaluationResultsStatusEnum.EVALUATION_IN_PROGRESS;
  }

  public isEvaluationResultsErrorStatus(): boolean {
    return !this.isEvaluationInProgress() && !this.isEvaluationResultsReceived();
  }

  public getConfidenceInterval(): string {
    const evaluationStatistics: EvaluationStatisticsDto = this.evaluationResults.evaluationStatisticsDto;
    return `[${evaluationStatistics.confidenceIntervalLowerBound}; ${evaluationStatistics.confidenceIntervalUpperBound}]`;
  }

  public getEvaluationStatisticsValue(row: string) {
    const evaluationStatistics: EvaluationStatisticsDto = this.evaluationResults.evaluationStatisticsDto;
    switch (row) {
      case "confidenceInterval":
        return this.getConfidenceInterval();
      default:
        return evaluationStatistics[row];
    }
  }

  private initEvaluationStatisticsRows(): void {
    this.evaluationStatisticsRows = [
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

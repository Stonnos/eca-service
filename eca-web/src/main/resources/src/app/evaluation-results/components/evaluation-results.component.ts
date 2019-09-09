import { Component, Input, OnInit } from '@angular/core';
import {
  EvaluationResultsDto, EvaluationStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { EvaluationResultsStatusEnum } from "../model/evaluation-results-status.enum";
import { EvaluationStatisticsFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { Utils } from "../../common/util/utils";

@Component({
  selector: 'app-evaluation-results',
  templateUrl: './evaluation-results.component.html',
  styleUrls: ['./evaluation-results.component.scss']
})
export class EvaluationResultsComponent implements OnInit {

  @Input()
  public evaluationResults: EvaluationResultsDto;

  public evaluationStatisticsFields: any[] = [];

  public constructor(private fieldService: FieldService) {
    this.initEvaluationStatisticsFields();
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
    if (evaluationStatistics.confidenceIntervalLowerBound && evaluationStatistics.confidenceIntervalUpperBound) {
      return `[${evaluationStatistics.confidenceIntervalLowerBound}; ${evaluationStatistics.confidenceIntervalUpperBound}]`;
    }
    return Utils.MISSING_VALUE;
  }

  public getEvaluationStatisticsValue(field: string) {
    const evaluationStatistics: EvaluationStatisticsDto = this.evaluationResults.evaluationStatisticsDto;
    if (field == EvaluationStatisticsFields.CONFIDENCE_INTERVAL) {
      return this.getConfidenceInterval();
    } else {
      return this.fieldService.getFieldValue(field, evaluationStatistics, Utils.MISSING_VALUE);
    }
  }

  private initEvaluationStatisticsFields(): void {
    this.evaluationStatisticsFields = [
      { name: EvaluationStatisticsFields.NUM_TEST_INSTANCES, label: "Число объектов тестовых данных:" },
      { name: EvaluationStatisticsFields.NUM_CORRECT, label: "Число правильно классифицированных объектов:" },
      { name: EvaluationStatisticsFields.NUM_INCORRECT, label: "Число неправильно классифицированных объектов:" },
      { name: EvaluationStatisticsFields.PCT_CORRECT, label: "Точность классификатора, %:" },
      { name: EvaluationStatisticsFields.PCT_INCORRECT, label: "Ошибка классификатора, %:" },
      { name: EvaluationStatisticsFields.MEAN_ABSOLUTE_ERROR, label: "Средняя абсолютная ошибка классификатора:" },
      { name: EvaluationStatisticsFields.ROOT_MEAN_SQUARED_ERROR, label: "Среднеквадратическая ошибка классификатора:" },
      { name: EvaluationStatisticsFields.VARIANCE_ERROR, label: "Дисперсия ошибки классификатора:" },
      { name: EvaluationStatisticsFields.CONFIDENCE_INTERVAL, label: "95% доверительный интервал ошибки классификатора:" },
    ];
  }
}

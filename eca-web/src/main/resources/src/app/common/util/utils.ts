import { EvaluationMethod } from "../model/evaluation-method.enum";
import { EnumDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

export class Utils {

  public static readonly MISSING_VALUE = "-";

  public static getEvaluationMethodDescription(evaluationMethod: EnumDto, numFolds: number, numTests: number): string {
    if (evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION) {
      if (numTests == 1) {
        return `${numFolds} блочная кросс - проверка`;
      } else {
        return `${numTests}×${numFolds} блочная кросс - проверка`;
      }
    } else {
      return evaluationMethod.description;
    }
  }
}

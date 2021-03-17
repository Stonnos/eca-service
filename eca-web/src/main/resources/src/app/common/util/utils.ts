import { EvaluationMethod } from "../model/evaluation-method.enum";
import {
  ClassifiersConfigurationDto,
  EnumDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { AuthenticationKeys } from "../../auth/model/auth.keys";

export class Utils {

  public static readonly LOGIN_REGEX: string = '[a-z0-9]{3,}';
  public static readonly EMAIL_REGEX: string =
    '[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})';
  public static readonly PERSON_NAME_REGEX: string = '([A-Z][a-z]+)|([А-Я][а-я]+)';
  public static readonly PERSON_NAME_MAX_LENGTH: number = 30;

  public static readonly MISSING_VALUE: string = "-";
  public static readonly PASSWORD_STRENGTH_CUTOFF: number = 3;

  public static getEvaluationMethodDescription(evaluationMethod: EnumDto, numFolds: number, numTests: number): string {
    if (evaluationMethod.value == EvaluationMethod.CROSS_VALIDATION) {
      if (numTests == 1) {
        return `${numFolds} - блочная кросс - проверка`;
      } else {
        return `${numTests}×${numFolds} - блочная кросс - проверка`;
      }
    } else {
      return evaluationMethod.description;
    }
  }

  public static getBearerTokenHeader() {
    return 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN);
  }

  public static getFileNameWithoutExtension(fileName: string): string {
    const extIndex: number = fileName.lastIndexOf('.');
    return extIndex > 0 && fileName.substring(0, extIndex);
  }

  public static getClassifiersConfigurationFile(classifiersConfiguration: ClassifiersConfigurationDto): string {
    return `${classifiersConfiguration.configurationName} configuration.xlsx`;
  }
}

import { Injectable } from '@angular/core';
import { ValidationErrorDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Injectable()
export class ValidationService {

  public hasError(errors: ValidationErrorDto[], field: string, code: string): boolean {
    return errors && errors.filter((validationError: ValidationErrorDto) => validationError.fieldName == field && validationError.code == code).length > 0;
  }

  public hasErrorCode(errors: ValidationErrorDto[], code: string): boolean {
    return errors && errors.filter((validationError: ValidationErrorDto) => validationError.code == code).length > 0;
  }

  public getFirstErrorCode(errors: ValidationErrorDto[], codes: string[]): string {
    return errors && errors.filter((validationError: ValidationErrorDto) => codes.includes(validationError.code))
      .map((validationError: ValidationErrorDto) => validationError.code)
      .pop();
  }

  public getErrorByCode(errors: ValidationErrorDto[], code: string): ValidationErrorDto {
    return errors && errors.filter((validationError: ValidationErrorDto) => validationError.code == code).pop();
  }
}

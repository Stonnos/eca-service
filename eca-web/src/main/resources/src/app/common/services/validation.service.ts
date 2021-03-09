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
}

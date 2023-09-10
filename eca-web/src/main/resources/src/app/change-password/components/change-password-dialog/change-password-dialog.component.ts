import { Component } from '@angular/core';
import { MessageService } from "primeng/api";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { BaseCreateDialogComponent } from "../../../common/dialog/base-create-dialog.component";
import { ChangePasswordRequest } from "../../model/change-password.request";
import { ChangePasswordService } from "../../services/change-password.service";
import {
  PasswordRuleResultDto,
  PasswordValidationErrorDto,
  ValidationErrorDto
} from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ValidationErrorCode } from "../../../common/model/validation-error-code";
import { ValidationService } from "../../../common/services/validation.service";
import { Utils } from "../../../common/util/utils";
import { ErrorHandler } from "../../../common/services/error-handler";

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrls: ['./change-password-dialog.component.scss']
})
export class ChangePasswordDialogComponent extends BaseCreateDialogComponent<ChangePasswordRequest> {

  public loading: boolean = false;

  public confirmPassword: string;

  public invalidPassword: boolean = false;

  public notSafePassword: boolean = false;

  public message: string;

  public errorCode: string;

  public passwordRegex: string = Utils.PASSWORD_REGEX;
  public passwordTooltipText: string = 'Разрешены только цифры, символы латинского алфавита и спец. символы кроме пробелов';

  public passwordValidationRuleDetails: PasswordRuleResultDto[] = [];

  private readonly errorCodes: string[] = [
    ValidationErrorCode.USER_LOCKED,
    ValidationErrorCode.ACTIVE_CHANGE_PASSWORD_REQUEST,
    ValidationErrorCode.PASSWORDS_MATCHED,
    ValidationErrorCode.NOT_SAFE_PASSWORD
  ];

  private readonly errorCodesMap = new Map<string, string>()
    .set(ValidationErrorCode.USER_LOCKED, 'Не удалось изменить пароль, т.к. ваш аккаунт заблокирован.')
    .set(ValidationErrorCode.PASSWORDS_MATCHED, 'Придумайте новый пароль отличный от старого.')
    .set(ValidationErrorCode.ACTIVE_CHANGE_PASSWORD_REQUEST, 'Вы уже отправили запрос на изменение пароля');

  public constructor(private messageService: MessageService,
                     private changePasswordService: ChangePasswordService,
                     private validationService: ValidationService,
                     private errorHandler: ErrorHandler) {
    super();
  }

  public ngOnInit(): void {
  }

  public hide(): void {
    this.errorCode = null;
    super.hide();
  }

  public submit(): void {
    this.submitted = true;
    if (this.isValid()) {
      this.loading = true;
      this.changePasswordService.changePassword(this.item)
        .pipe(
          finalize(() => {
            this.loading = false;
          })
        )
        .subscribe({
          next: () => {
            this.itemEvent.emit();
            this.hide();
          },
          error: (error) => {
            this.handleError(error);
          }
        });
    }
  }

  public onOldPasswordFocus(event): void {
    this.invalidPassword = false;
  }

  public getErrorMessage(): string {
    return this.errorCode && this.errorCodesMap.get(this.errorCode);
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse && error.status === 400) {
      const errors: ValidationErrorDto[] = error.error;
      this.invalidPassword = this.validationService.hasErrorCode(errors, ValidationErrorCode.INVALID_PASSWORD);
      this.handlePasswordValidationError(errors);
      this.errorCode = this.errorHandler.getFirstErrorCode(error, this.errorCodes);
      this.message = this.errorCodesMap.get(this.errorCode);
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    }
  }

  private handlePasswordValidationError(errors: ValidationErrorDto[]): void {
    if (this.validationService.hasErrorCode(errors, ValidationErrorCode.NOT_SAFE_PASSWORD)) {
      this.notSafePassword = true;
      const error = errors.pop();
      const passwordValidationError = error as PasswordValidationErrorDto;
      this.passwordValidationRuleDetails = passwordValidationError.details;
    }
  }
}

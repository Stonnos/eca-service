import { Component, Input } from '@angular/core';
import { MessageService } from "primeng/api";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { BaseCreateDialogComponent } from "../../../common/dialog/base-create-dialog.component";
import { ChangePasswordRequest } from "../../model/change-password.request";
import { ChangePasswordService } from "../../services/change-password.service";
import {
  ChangePasswordRequestStatusDto,
  PasswordRuleResultDto,
  PasswordValidationErrorDto,
  ValidationErrorDto
} from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ValidationErrorCode } from "../../../common/model/validation-error-code";
import { ValidationService } from "../../../common/services/validation.service";
import { Utils } from "../../../common/util/utils";
import { ErrorHandler } from "../../../common/services/error-handler";
import { LogoutService } from "../../../auth/services/logout.service";

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrls: ['./change-password-dialog.component.scss']
})
export class ChangePasswordDialogComponent extends BaseCreateDialogComponent<ChangePasswordRequest> {

  private static readonly CHANGE_PASSWORD_STEP: string = 'change-password';
  private static readonly CONFIRM_CHANGE_PASSWORD_STEP: string = 'confirm-change-password';

  @Input()
  public step: string = ChangePasswordDialogComponent.CHANGE_PASSWORD_STEP;
  @Input()
  public token: string;

  public changePasswordRequestCreatedMessage: string =
    'На ваш email отправлено письмо с кодом подтверждением для смены пароля';

  public confirmationCode: string;
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
    ValidationErrorCode.NOT_SAFE_PASSWORD,
    ValidationErrorCode.INVALID_TOKEN,
    ValidationErrorCode.INVALID_CONFIRMATION_CODE
  ];

  private readonly errorCodesMap = new Map<string, string>()
    .set(ValidationErrorCode.USER_LOCKED, 'Не удалось изменить пароль, т.к. ваш аккаунт заблокирован.')
    .set(ValidationErrorCode.PASSWORDS_MATCHED, 'Придумайте новый пароль отличный от старого.')
    .set(ValidationErrorCode.ACTIVE_CHANGE_PASSWORD_REQUEST, 'Вы уже отправили запрос на изменение пароля')
    .set(ValidationErrorCode.INVALID_TOKEN, 'Запрос с заданным токеном не существует')
    .set(ValidationErrorCode.INVALID_CONFIRMATION_CODE, 'Неправильный код');

  public constructor(private messageService: MessageService,
                     private changePasswordService: ChangePasswordService,
                     private validationService: ValidationService,
                     private errorHandler: ErrorHandler,
                     private logoutService: LogoutService) {
    super();
  }

  public ngOnInit(): void {
  }

  public hide(): void {
    this.errorCode = null;
    this.message = null;
    super.hide();
  }

  public submit(): void {
    this.errorCode = null;
    this.message = null;
    this.submitted = true;
    if (this.isValid()) {
      if (this.step == ChangePasswordDialogComponent.CHANGE_PASSWORD_STEP) {
        this.changePassword();
      } else {
        this.confirmChangePasswordRequest();
      }
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

  private changePassword(): void {
    this.loading = true;
    this.changePasswordService.changePassword(this.item)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (changePasswordRequestStatusDto: ChangePasswordRequestStatusDto) => {
          this.token = changePasswordRequestStatusDto.token;
          this.step = ChangePasswordDialogComponent.CONFIRM_CHANGE_PASSWORD_STEP;
        },
        error: (error) => {
          this.handleError(error);
        }
      });
  }

  private confirmChangePasswordRequest(): void {
    this.loading = true;
    this.changePasswordService.confirmChangePasswordRequest(this.token, this.confirmationCode)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.hide();
          this.logoutService.logout();
          this.messageService.add({ severity: 'info', summary: `Пароль был успешно изменен`, detail: '' });
        },
        error: (error) => {
          this.handleConfirmationCodeError(error);
        }
      });
  }

  private handleConfirmationCodeError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 400) {
        this.errorCode = this.errorHandler.getFirstErrorCode(error, this.errorCodes);
        this.message = this.errorCodesMap.get(this.errorCode);
      } else {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    }
  }
}

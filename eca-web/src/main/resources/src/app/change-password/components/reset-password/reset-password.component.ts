import { Component, OnInit, ViewChild } from '@angular/core';
import { MessageService } from "primeng/api";
import { BaseForm } from "../../../common/form/base-form";
import { NgForm } from "@angular/forms";
import { ResetPasswordService } from "../../services/reset-password.service";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { ResetPasswordRequest } from "../../model/reset-password.request";
import { ActivatedRoute } from "@angular/router";
import { Utils } from "../../../common/util/utils";
import { LogoutService } from "../../../auth/services/logout.service";
import { ValidationErrorCode } from "../../../common/model/validation-error-code";
import { ValidationService } from "../../../common/services/validation.service";
import { ErrorHandler } from "../../../common/services/error-handler";
import {
  PasswordRuleResultDto, PasswordValidationErrorDto,
  ValidationErrorDto
} from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements BaseForm, OnInit {

  public submitted: boolean = false;
  public loading: boolean = false;
  public tokenValid: boolean = false;

  @ViewChild(NgForm, { static: true })
  public form: NgForm;

  public notSafePassword: boolean = false;

  public token: string;
  public password: string;
  public confirmPassword: string;

  public message: string;

  public errorCode: string;

  public passwordRegex: string = Utils.PASSWORD_REGEX;
  public passwordTooltipText: string = 'Разрешены только цифры, символы латинского алфавита и спец. символы кроме пробелов';

  public passwordValidationRuleDetails: PasswordRuleResultDto[] = [];

  private readonly errorCodes: string[] = [
    ValidationErrorCode.USER_LOCKED,
    ValidationErrorCode.PASSWORDS_MATCHED
  ];

  private readonly errorCodesMap = new Map<string, string>()
    .set(ValidationErrorCode.USER_LOCKED, 'Не удалось изменить пароль, т.к. ваш аккаунт заблокирован.')
    .set(ValidationErrorCode.PASSWORDS_MATCHED, 'Придумайте новый пароль отличный от старого.');


  public constructor(private messageService: MessageService,
                     private resetPasswordService: ResetPasswordService,
                     private logoutService: LogoutService,
                     private validationService: ValidationService,
                     private errorHandler: ErrorHandler,
                     private route: ActivatedRoute) {
    this.token = this.route.snapshot.queryParams['token'];
  }

  public ngOnInit(): void {
    this.verifyToken();
  }

  public clear(): void {
    this.submitted = false;
  }

  public isValid(): boolean {
    return this.form.valid;
  }

  public submit(): void {
    this.submitted = true;
    if (this.isValid()) {
      this.loading = true;
      const resetPasswordRequest: ResetPasswordRequest = new ResetPasswordRequest(this.token, this.password);
      this.resetPasswordService.resetPassword(resetPasswordRequest)
        .pipe(
          finalize(() => {
            this.loading = false;
          })
        )
        .subscribe({
          next: () => {
            this.clear();
            this.logoutService.logout();
            this.messageService.add({ severity: 'info', summary: `Пароль был успешно сброшен`, detail: '' });
          },
          error: (error) => {
            this.handleError(error);
          }
        });
    }
  }

  public getErrorMessage(): string {
    return this.errorCode && this.errorCodesMap.get(this.errorCode);
  }

  private verifyToken(): void {
    this.loading = true;
    this.resetPasswordService.verifyToken(this.token)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (tokenValid: boolean) => {
          this.tokenValid = tokenValid;
        },
        error: (error) => {
          this.handleError(error);
        }
      });
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse && error.status === 400) {
      const errors: ValidationErrorDto[] = error.error;
      this.tokenValid = !this.validationService.hasErrorCode(errors, ValidationErrorCode.INVALID_TOKEN);
      this.notSafePassword = this.validationService.hasErrorCode(errors, ValidationErrorCode.NOT_SAFE_PASSWORD);
      if (this.notSafePassword) {
        this.handlePasswordValidationError(errors);
      }
      this.errorCode = this.errorHandler.getFirstErrorCode(error, this.errorCodes);
      this.message = this.errorCodesMap.get(this.errorCode);
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    }
  }

  private handlePasswordValidationError(errors: ValidationErrorDto[]): void {
    const error = errors.pop();
    const passwordValidationError = error as PasswordValidationErrorDto;
    this.passwordValidationRuleDetails = passwordValidationError.details;
  }
}

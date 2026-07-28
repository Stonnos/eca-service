import { Component, OnInit, ViewChild } from '@angular/core';
import { MessageService } from "primeng/api";
import { NgForm } from "@angular/forms";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { Router } from '@angular/router';
import { BaseForm } from '../../common/form/base-form';
import { Utils } from '../../common/util/utils';
import { ValidationErrorCode } from '../../common/model/validation-error-code';
import { PasswordRuleResultDto, ValidationErrorDto, PasswordValidationErrorDto } from '../../../../../../../target/generated-sources/typescript/eca-web-dto';
import { ForceSetPasswordService } from '../services/force-set-password.service';
import { ValidationService } from '../../common/services/validation.service';
import { ErrorHandler} from '../../common/services/error-handler';
import { ForceSetPasswordRequest } from '../model/force-set-password.request';
import { GlobalStateService } from '../../common/services/global-state.service';
import { GlobalVariables } from '../../common/util/global-variables';

@Component({
  selector: 'app-reset-password',
  templateUrl: './set-password.component.html',
  styleUrls: ['./set-password.component.scss']
})
export class SetPasswordComponent implements BaseForm, OnInit {

  public submitted: boolean = false;
  public loading: boolean = false;
  public tokenValid: boolean = false;

  @ViewChild(NgForm, { static: true })
  public form: NgForm;

  public notSafePassword: boolean = false;

  public token: string;
  public confirmationCode: string;
  public password: string;
  public confirmPassword: string;

  public message: string;

  public errorCode: string;

  public passwordRegex: string = Utils.PASSWORD_REGEX;
  public passwordTooltipText: string = 'Разрешены только цифры, символы латинского алфавита и спец. символы кроме пробелов';

  public passwordValidationRuleDetails: PasswordRuleResultDto[] = [];

  private readonly errorCodes: string[] = [
    ValidationErrorCode.USER_LOCKED,
    ValidationErrorCode.PASSWORDS_MATCHED,
    ValidationErrorCode.INVALID_CONFIRMATION_CODE
  ];

  private readonly errorCodesMap = new Map<string, string>()
    .set(ValidationErrorCode.USER_LOCKED, 'Не удалось установить пароль, т.к. ваш аккаунт заблокирован.')
    .set(ValidationErrorCode.PASSWORDS_MATCHED, 'Придумайте новый пароль отличный от старого.')
    .set(ValidationErrorCode.INVALID_CONFIRMATION_CODE, 'Неправильный код');


  public constructor(private messageService: MessageService,
                     private forceSetPasswordService: ForceSetPasswordService,
                     private validationService: ValidationService,
                     private globalStateService: GlobalStateService,
                     private errorHandler: ErrorHandler,
                     private router: Router) {
  }

  public ngOnInit(): void {
    this.token = this.globalStateService.getValue(GlobalVariables.SET_PASSWORD_TOKEN);
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
      const forceSetPasswordRequest: ForceSetPasswordRequest = new ForceSetPasswordRequest(this.token, this.confirmationCode, this.password);
      this.forceSetPasswordService.forceSetPassword(forceSetPasswordRequest)
        .pipe(
          finalize(() => {
            this.loading = false;
          })
        )
        .subscribe({
          next: () => {
            this.clear();
            this.globalStateService.remove(GlobalVariables.SET_PASSWORD_TOKEN);
            this.messageService.add({ severity: 'info', summary: `Пароль был успешно установлен`, detail: '' });
            this.router.navigate(['/login']);
          },
          error: (error) => {
            this.handleError(error);
          }
        });
    }
  }

  private verifyToken(): void {
    this.loading = true;
    this.forceSetPasswordService.verifyToken(this.token)
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

  public getErrorMessage(): string {
    return this.errorCode && this.errorCodesMap.get(this.errorCode);
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse && error.status === 400) {
      const errors: ValidationErrorDto[] = error.error;
      this.tokenValid = !this.validationService.hasErrorCode(errors, ValidationErrorCode.INVALID_TOKEN);
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
    } else {
      this.notSafePassword = false;
    }
  }
}

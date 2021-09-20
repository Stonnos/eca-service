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

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements BaseForm, OnInit {

  public submitted: boolean = false;
  public loading: boolean = false;
  public tokenValid: boolean = false;
  public userLocked: boolean = false;

  @ViewChild(NgForm, { static: true })
  public form: NgForm;

  public safePassword: boolean = false;

  public token: string;
  public password: string;
  public confirmPassword: string;

  public constructor(private messageService: MessageService,
                     private resetPasswordService: ResetPasswordService,
                     private logoutService: LogoutService,
                     private validationService: ValidationService,
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
    if (this.isValid() && this.safePassword) {
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
            this.messageService.add({ severity: 'info', summary: `Пароль был успешно восстановлен`, detail: '' });
          },
          error: (error) => {
            this.handleError(error);
          }
        });
    }
  }

  public onStrengthChange(score: number): void {
    this.safePassword = score >= Utils.PASSWORD_STRENGTH_CUTOFF;
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
      this.tokenValid = !this.validationService.hasErrorCode(error.error, ValidationErrorCode.INVALID_TOKEN);
      this.userLocked = this.validationService.hasErrorCode(error.error, ValidationErrorCode.USER_LOCKED);
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    }
  }
}

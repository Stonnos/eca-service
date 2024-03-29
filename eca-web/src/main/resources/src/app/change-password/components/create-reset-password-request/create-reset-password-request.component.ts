import { Component, OnInit, ViewChild } from '@angular/core';
import { MessageService } from "primeng/api";
import { ValidationService } from "../../../common/services/validation.service";
import { BaseForm } from "../../../common/form/base-form";
import { NgForm } from "@angular/forms";
import { ResetPasswordService } from "../../services/reset-password.service";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { UserFields } from "../../../common/util/field-names";
import { ValidationErrorCode } from "../../../common/model/validation-error-code";
import { CreateResetPasswordRequest } from "../../model/create-reset-password.request";

@Component({
  selector: 'app-create-reset-password-request',
  templateUrl: './create-reset-password-request.component.html',
  styleUrls: ['./create-reset-password-request.component.scss']
})
export class CreateResetPasswordRequestComponent implements BaseForm, OnInit {

  public submitted: boolean = false;
  public loading: boolean = false;
  public sent: boolean = false;

  public emailNotExists: boolean = false;
  public userLocked: boolean = false;
  public hasActiveResetPasswordRequest: boolean = false;

  @ViewChild(NgForm, { static: true })
  public form: NgForm;

  public createResetPasswordRequest: CreateResetPasswordRequest = new CreateResetPasswordRequest();

  public constructor(private messageService: MessageService,
                     private validationService: ValidationService,
                     private resetPasswordService: ResetPasswordService) {
  }

  public ngOnInit(): void {
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
      this.resetPasswordService.createResetPasswordRequest(this.createResetPasswordRequest)
        .pipe(
          finalize(() => {
            this.loading = false;
          })
        )
        .subscribe({
          next: () => {
            this.sent = true;
            this.clear();
          },
          error: (error) => {
            this.handleError(error);
          }
        });
    }
  }

  public onEmailFocus(event): void {
    this.emailNotExists = false;
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse && error.status === 400) {
      this.emailNotExists = this.validationService.hasError(error.error, UserFields.EMAIL, ValidationErrorCode.USER_EMAIL);
      this.userLocked = this.validationService.hasErrorCode(error.error, ValidationErrorCode.USER_LOCKED);
      this.hasActiveResetPasswordRequest =
        this.validationService.hasErrorCode(error.error, ValidationErrorCode.ACTIVE_RESET_PASSWORD_REQUEST);
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    }
  }
}

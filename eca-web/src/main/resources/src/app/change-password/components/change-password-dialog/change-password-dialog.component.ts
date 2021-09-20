import { Component } from '@angular/core';
import { MessageService } from "primeng/api";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { BaseCreateDialogComponent } from "../../../common/dialog/base-create-dialog.component";
import { ChangePasswordRequest } from "../../model/change-password.request";
import { ChangePasswordService } from "../../services/change-password.service";
import { ValidationErrorDto } from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ValidationErrorCode } from "../../../common/model/validation-error-code";
import { ValidationService } from "../../../common/services/validation.service";
import { Utils } from "../../../common/util/utils";

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrls: ['./change-password-dialog.component.scss']
})
export class ChangePasswordDialogComponent extends BaseCreateDialogComponent<ChangePasswordRequest> {

  public loading: boolean = false;

  public confirmPassword: string;

  public invalidPassword: boolean = false;
  public hasActiveChangePasswordRequest: boolean = false;

  public safePassword: boolean = false;

  public constructor(private messageService: MessageService,
                     private changePasswordService: ChangePasswordService,
                     private validationService: ValidationService) {
    super();
  }

  public ngOnInit(): void {
  }

  public hide(): void {
    this.hasActiveChangePasswordRequest = false;
    super.hide();
  }

  public submit(): void {
    this.submitted = true;
    if (this.isValid() && this.safePassword) {
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

  public onStrengthChange(score: number): void {
    this.safePassword = score >= Utils.PASSWORD_STRENGTH_CUTOFF;
  }

  public onOldPasswordFocus(event): void {
    this.invalidPassword = false;
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse && error.status === 400) {
      const errors: ValidationErrorDto[] = error.error;
      this.invalidPassword = this.validationService.hasErrorCode(errors, ValidationErrorCode.INVALID_PASSWORD);
      this.hasActiveChangePasswordRequest =
        this.validationService.hasErrorCode(errors, ValidationErrorCode.ACTIVE_CHANGE_PASSWORD_REQUEST);
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    }
  }
}

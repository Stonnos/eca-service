import { Component, OnInit } from '@angular/core';
import { BaseCreateDialogComponent } from "../../../common/dialog/base-create-dialog.component";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { MessageService } from "primeng/api";
import { ValidationErrorDto } from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ValidationService } from "../../../common/services/validation.service";
import { ValidationErrorCode } from "../../../common/model/validation-error-code";
import { ChangeEmailService } from "../../services/change-email.service";

@Component({
  selector: 'app-update-user-email',
  templateUrl: './update-user-email.component.html',
  styleUrls: ['./update-user-email.component.scss']
})
export class UpdateUserEmailComponent extends BaseCreateDialogComponent<string> implements OnInit {

  public emailRegex: string = '[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})';

  public loading: boolean = false;

  public errorCode: string;

  private readonly validationErrorCodes: string[] = [
    ValidationErrorCode.UNIQUE_EMAIL,
    ValidationErrorCode.EMAIL_ALREADY_BOUND,
    ValidationErrorCode.ACTIVE_CHANGE_EMAIL_REQUEST,
  ];

  private readonly errorsMap = new Map<string, string>()
    .set(ValidationErrorCode.UNIQUE_EMAIL, 'Пользователь с таким Email уже вуществует')
    .set(ValidationErrorCode.EMAIL_ALREADY_BOUND, 'Этот Email уже привязан к вашему аккаунту')
    .set(ValidationErrorCode.ACTIVE_CHANGE_EMAIL_REQUEST, 'Вы уже отправили запрос на изменение Email');


  public constructor(private changeEmailService: ChangeEmailService,
                     private messageService: MessageService,
                     private validationService: ValidationService) {
    super();
  }

  public ngOnInit(): void {
  }

  public submit() {
    this.submitted = true;
    if (this.isValid()) {
      this.loading = true;
      this.changeEmailService.changeEmail(this.item)
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

  public onEmailFocus(event): void {
    this.errorCode = null;
  }

  public getErrorMessage(): string {
    return this.errorCode && this.errorsMap.get(this.errorCode);
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 400) {
        const errors: ValidationErrorDto[] = error.error;
        this.errorCode = this.validationService.getFirstErrorCode(errors, this.validationErrorCodes);
      } else {
        this.handleUnknownError(error);
      }
    } else {
      this.handleUnknownError(error);
    }
  }

  private handleUnknownError(error): void {
    this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
  }
}

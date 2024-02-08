import { Component, Input, OnInit } from '@angular/core';
import { BaseCreateDialogComponent } from "../../../common/dialog/base-create-dialog.component";
import { finalize } from "rxjs/operators";
import { MessageService } from "primeng/api";
import { ValidationErrorCode } from "../../../common/model/validation-error-code";
import { ChangeEmailService } from "../../services/change-email.service";
import { ErrorHandler } from "../../../common/services/error-handler";
import { ChangeEmailRequestStatusDto } from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-update-user-email',
  templateUrl: './update-user-email.component.html',
  styleUrls: ['./update-user-email.component.scss']
})
export class UpdateUserEmailComponent extends BaseCreateDialogComponent<string> implements OnInit {

  private static readonly CHANGE_EMAIL_STEP: string = 'change-email';
  private static readonly CONFIRM_CHANGE_EMAIL_STEP: string = 'confirm-change-email';

  @Input()
  public step: string = UpdateUserEmailComponent.CHANGE_EMAIL_STEP;
  @Input()
  public token: string;

  public changeEmailRequestCreatedMessage: string =
    'На ваш email отправлено письмо с кодом подтверждением для изменения email';

  public confirmationCode: string;

  public emailRegex: string = '[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})';

  public loading: boolean = false;

  public errorCode: string;

  private readonly validationErrorCodes: string[] = [
    ValidationErrorCode.UNIQUE_EMAIL,
    ValidationErrorCode.EMAIL_ALREADY_BOUND,
    ValidationErrorCode.ACTIVE_CHANGE_EMAIL_REQUEST,
    ValidationErrorCode.INVALID_TOKEN,
    ValidationErrorCode.INVALID_CONFIRMATION_CODE
  ];

  private readonly errorsMap = new Map<string, string>()
    .set(ValidationErrorCode.UNIQUE_EMAIL, 'Пользователь с таким Email уже вуществует')
    .set(ValidationErrorCode.EMAIL_ALREADY_BOUND, 'Этот Email уже привязан к вашему аккаунту')
    .set(ValidationErrorCode.ACTIVE_CHANGE_EMAIL_REQUEST, 'Вы уже отправили запрос на изменение Email')
    .set(ValidationErrorCode.INVALID_TOKEN, 'Запрос с заданным токеном не существует')
    .set(ValidationErrorCode.INVALID_CONFIRMATION_CODE, 'Неправильный код');


  public constructor(private changeEmailService: ChangeEmailService,
                     private messageService: MessageService,
                     private errorHandler: ErrorHandler) {
    super();
  }

  public ngOnInit(): void {
  }

  public hide(): void {
    this.errorCode = null;
    super.hide();
  }

  public submit() {
    this.errorCode = null;
    this.submitted = true;
    if (this.isValid()) {
      if (this.step == UpdateUserEmailComponent.CHANGE_EMAIL_STEP) {
        this.changeEmailRequest();
      } else {
        this.confirmChangePasswordRequest();
      }
    }
  }

  public onEmailFocus(event): void {
    this.errorCode = null;
  }

  public getErrorMessage(): string {
    return this.errorCode && this.errorsMap.get(this.errorCode);
  }

  private handleError(error): void {
    this.errorCode = this.errorHandler.getFirstErrorCode(error, this.validationErrorCodes);
  }

  private changeEmailRequest(): void {
    this.loading = true;
    this.changeEmailService.changeEmail(this.item)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (changeEmailRequestStatusDto: ChangeEmailRequestStatusDto) => {
          this.token = changeEmailRequestStatusDto.token;
          this.step = UpdateUserEmailComponent.CONFIRM_CHANGE_EMAIL_STEP;
        },
        error: (error) => {
          this.handleError(error);
        }
      });
  }

  private confirmChangePasswordRequest(): void {
    this.loading = true;
    this.changeEmailService.confirmChangeEmailRequest(this.token, this.confirmationCode)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.messageService.add({ severity: 'info', summary: `Email был успешно изменен`, detail: '' });
          this.itemEvent.emit(this.item);
          this.hide();
        },
        error: (error) => {
          this.handleError(error);
        }
      });
  }
}

import { Component, OnInit } from '@angular/core';
import { MessageService } from "primeng/api";
import { ActivatedRoute } from "@angular/router";
import { ChangePasswordService } from "../../services/change-password.service";
import { LogoutService } from "../../../auth/services/logout.service";
import { finalize } from "rxjs/operators";
import { ValidationErrorCode } from "../../../common/model/validation-error-code";
import { ErrorHandler } from "../../../common/services/error-handler";

@Component({
  selector: 'app-confirm-change-password',
  templateUrl: './confirm-change-password.component.html',
  styleUrls: ['./confirm-change-password.component.scss']
})
export class ConfirmChangePasswordComponent implements OnInit {

  public loading: boolean = true;
  public header = 'Подтверждение смены пароля';
  public message: string;

  private errorCode: string;

  private readonly passwordChangedMessageLife: number = 10000;

  private readonly errorCodes: string[] = [
    ValidationErrorCode.INVALID_TOKEN,
    ValidationErrorCode.USER_LOCKED
  ];

  private readonly errorCodesMap = new Map<string, string>()
    .set(ValidationErrorCode.INVALID_TOKEN, 'Не удалось изменить пароль, т.к. ссылка недействительна.')
    .set(ValidationErrorCode.USER_LOCKED, 'Не удалось изменить пароль, т.к. ваш аккаунт заблокирован.');

  public constructor(private changePasswordService: ChangePasswordService,
                     private messageService: MessageService,
                     private logoutService: LogoutService,
                     private errorHandler: ErrorHandler,
                     private route: ActivatedRoute) {
  }

  public ngOnInit(): void {
    this.confirmChangePasswordRequest();
  }

  private confirmChangePasswordRequest(): void {
    this.loading = true;
    const token = this.route.snapshot.queryParams['token'];
    this.changePasswordService.confirmChangePasswordRequest(token)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.logoutService.logout();
          this.messageService.add({ severity: 'info', summary: `Пароль был успешно изменен`, detail: '', life: this.passwordChangedMessageLife });
        },
        error: (error) => {
          this.errorCode = this.errorHandler.getFirstErrorCode(error, this.errorCodes);
          this.message = this.errorCodesMap.get(this.errorCode);
        }
      });
  }
}

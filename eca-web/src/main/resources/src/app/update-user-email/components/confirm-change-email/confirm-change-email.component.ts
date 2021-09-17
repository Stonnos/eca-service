import { Component, OnInit } from '@angular/core';
import { MessageService } from "primeng/api";
import { ActivatedRoute, Router } from "@angular/router";
import { LogoutService } from "../../../auth/services/logout.service";
import { finalize } from "rxjs/operators";
import { ValidationErrorCode } from "../../../common/model/validation-error-code";
import { ChangeEmailService } from "../../services/change-email.service";
import { ErrorHandler } from "../../../common/services/error-handler";

@Component({
  selector: 'app-confirm-change-email',
  templateUrl: './confirm-change-email.component.html',
  styleUrls: ['./confirm-change-email.component.scss']
})
export class ConfirmChangeEmailComponent implements OnInit {

  public loading: boolean = true;
  public header = 'Подтверждение смены Email';
  public message: string;

  private errorCode: string;

  private readonly errorCodes: string[] = [
    ValidationErrorCode.INVALID_TOKEN,
    ValidationErrorCode.USER_LOCKED
  ];

  private readonly errorCodesMap = new Map<string, string>()
    .set(ValidationErrorCode.INVALID_TOKEN, 'Не удалось изменить Email, т.к. ссылка недействительна.')
    .set(ValidationErrorCode.USER_LOCKED, 'Не удалось изменить Email, т.к. ваш аккаунт заблокирован.');

  public constructor(private changeEmailService: ChangeEmailService,
                     private messageService: MessageService,
                     private logoutService: LogoutService,
                     private errorHandler: ErrorHandler,
                     private route: ActivatedRoute,
                     private router: Router) {
  }

  public ngOnInit(): void {
    this.confirmChangePasswordRequest();
  }

  private confirmChangePasswordRequest(): void {
    this.loading = true;
    const token = this.route.snapshot.queryParams['token'];
    this.changeEmailService.confirmChangeEmailRequest(token)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.messageService.add({ severity: 'info', summary: `Email был успешно изменен`, detail: '' });
          this.router.navigate(['/dashboard/profile']);
        },
        error: (error) => {
          this.errorCode = this.errorHandler.getFirstErrorCode(error, this.errorCodes);
          this.message = this.errorCodesMap.get(this.errorCode);
        }
      });
  }
}

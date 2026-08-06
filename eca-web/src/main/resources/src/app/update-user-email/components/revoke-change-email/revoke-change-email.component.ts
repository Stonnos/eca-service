import { Component, OnInit } from '@angular/core';
import { MessageService } from "primeng/api";
import { ActivatedRoute } from "@angular/router";
import { LogoutService } from "../../../auth/services/logout.service";
import { finalize } from "rxjs/operators";
import { ValidationErrorCode } from "../../../common/model/validation-error-code";
import { ErrorHandler } from "../../../common/services/error-handler";
import { ChangeEmailService } from '../../services/change-email.service';

@Component({
  selector: 'app-revoke-change-email',
  templateUrl: './revoke-change-email.component.html',
  styleUrls: ['./revoke-change-email.component.scss']
})
export class RevokeChangeEmailComponent implements OnInit {

  public loading: boolean = true;
  public header = 'Отмена изменения email';
  public message: string;

  private errorCode: string;

  private readonly errorCodes: string[] = [
    ValidationErrorCode.INVALID_TOKEN,
    ValidationErrorCode.USER_LOCKED
  ];

  private readonly errorCodesMap = new Map<string, string>()
    .set(ValidationErrorCode.INVALID_TOKEN, 'Не удалось отменить изенение email, т.к. ссылка недействительна.');

  public constructor(private changeEmailService: ChangeEmailService,
                     private messageService: MessageService,
                     private logoutService: LogoutService,
                     private errorHandler: ErrorHandler,
                     private route: ActivatedRoute) {
  }

  public ngOnInit(): void {
    this.revokeChangeEmail();
  }

  private revokeChangeEmail(): void {
    this.loading = true;
    const token = this.route.snapshot.queryParams['token'];
    this.changeEmailService.revokeChangeEmail(token)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.logoutService.logout();
          this.messageService.add({ severity: 'info', summary: `Изменение email отменено`, detail: '' });
        },
        error: (error) => {
          this.errorCode = this.errorHandler.getFirstErrorCode(error, this.errorCodes);
          this.message = this.errorCodesMap.get(this.errorCode);
        }
      });
  }
}

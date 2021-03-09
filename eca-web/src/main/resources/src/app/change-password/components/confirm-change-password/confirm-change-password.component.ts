import { Component, OnInit } from '@angular/core';
import { MessageService } from "primeng/api";
import { HttpErrorResponse } from "@angular/common/http";
import { ActivatedRoute, Router } from "@angular/router";
import { ChangePasswordService } from "../../services/change-password.service";
import { LogoutService } from "../../../auth/services/logout.service";
import { finalize } from "rxjs/operators";

@Component({
  selector: 'app-confirm-change-password',
  templateUrl: './confirm-change-password.component.html',
  styleUrls: ['./confirm-change-password.component.scss']
})
export class ConfirmChangePasswordComponent implements OnInit {

  public token: string;
  public tokenValid: boolean = false;
  public loading: boolean = true;

  public constructor(private changePasswordService: ChangePasswordService,
                     private messageService: MessageService,
                     private logoutService: LogoutService,
                     private route: ActivatedRoute,
                     private router: Router) {
    this.token = this.route.snapshot.queryParams['token'];
  }

  public ngOnInit(): void {
    this.confirmChangePasswordRequest();
  }

  private confirmChangePasswordRequest(): void {
    this.loading = true;
    this.changePasswordService.confirmChangePasswordRequest(this.token)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.logoutService.logout();
          this.messageService.add({ severity: 'success', summary: `Пароль был успешно изменен`, detail: '' });
        },
        error: (error) => {
          this.handleError(error);
        }
      });
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 400) {
        this.tokenValid = false;
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

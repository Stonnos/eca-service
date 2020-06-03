import { Component, OnInit, ViewChild } from '@angular/core';
import { MessageService } from "primeng/api";
import { BaseForm } from "../../../common/form/base-form";
import { NgForm } from "@angular/forms";
import { ResetPasswordService } from "../../services/reset-password.service";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { ResetPasswordRequest } from "../../model/reset-password.request";
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements BaseForm, OnInit {

  public submitted: boolean = false;
  public loading: boolean = false;
  public tokenInvalid: boolean = false;

  @ViewChild(NgForm, { static: true })
  public form: NgForm;

  public passwordScoreCutoff: number = 3;
  public safePassword: boolean = false;

  public token: string;
  public password: string;
  public confirmPassword: string;

  public constructor(private messageService: MessageService,
                     private resetPasswordService: ResetPasswordService,
                     private route: ActivatedRoute) {
    this.token = this.route.snapshot.params.token;
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
            this.tokenInvalid = false;
            this.clear();
          },
          error: (error) => {
            this.handleError(error);
          }
        });
    }
  }

  public onStrengthChange(score: number): void {
    this.safePassword = score >= this.passwordScoreCutoff;
  }

  private handleError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 400) {
        this.tokenInvalid = true;
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

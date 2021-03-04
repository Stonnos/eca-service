import { Component, OnInit, ViewChild } from '@angular/core';
import { MessageService } from "primeng/api";
import { BaseForm } from "../../../common/form/base-form";
import { NgForm } from "@angular/forms";
import { ResetPasswordService } from "../../services/reset-password.service";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { ResetPasswordRequest } from "../../model/reset-password.request";
import { ActivatedRoute, Router } from "@angular/router";
import {BaseCreateDialogComponent} from "../../../common/dialog/base-create-dialog.component";
import {ChangePasswordRequest} from "../../model/change-password.request";

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrls: ['./change-password-dialog.component.scss']
})
export class ChangePasswordDialogComponent extends BaseCreateDialogComponent<ChangePasswordRequest> {

  public loading: boolean = false;

  public confirmPassword: string;

  public passwordScoreCutoff: number = 3;
  public safePassword: boolean = false;

  public constructor(private messageService: MessageService,
                     private resetPasswordService: ResetPasswordService,
                     private route: ActivatedRoute,
                     private router: Router) {
    super();
  }

  public ngOnInit(): void {
  }

  public onStrengthChange(score: number): void {
    this.safePassword = score >= this.passwordScoreCutoff;
  }
}

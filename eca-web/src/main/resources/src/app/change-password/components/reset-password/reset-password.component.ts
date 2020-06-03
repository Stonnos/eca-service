import { Component, OnInit, ViewChild } from '@angular/core';
import { MessageService } from "primeng/api";
import { ValidationService } from "../../../common/services/validation.service";
import { BaseForm } from "../../../common/form/base-form";
import { NgForm } from "@angular/forms";
import { ForgotPasswordRequest } from "../../model/forgot-password.request";
import { ResetPasswordService } from "../../services/reset-password.service";
import { finalize } from "rxjs/operators";
import { HttpErrorResponse } from "@angular/common/http";
import { UserFields } from "../../../common/util/field-names";
import { ValidationErrorCode } from "../../../common/model/validation-error-code";
import {ResetPasswordRequest} from "../../model/reset-password.request";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements BaseForm, OnInit {

  public submitted: boolean = false;
  public loading: boolean = false;

  @ViewChild(NgForm, { static: true })
  public form: NgForm;

  public resetPasswordRequest: ResetPasswordRequest = new ResetPasswordRequest();

  public constructor(private messageService: MessageService,
                     private resetPasswordService: ResetPasswordService,
                     private route: ActivatedRoute) {
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

    }
  }
}

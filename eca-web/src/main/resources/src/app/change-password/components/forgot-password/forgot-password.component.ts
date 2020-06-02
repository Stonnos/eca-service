import { Component, OnInit, ViewChild } from '@angular/core';
import { MessageService } from "primeng/api";
import { ValidationService } from "../../../common/services/validation.service";
import { BaseForm } from "../../../common/form/base-form";
import { NgForm } from "@angular/forms";
import { ForgotPasswordRequest } from "../model/forgot-password.request";

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements BaseForm, OnInit {

  public submitted: boolean = false;
  public loading: boolean = false;
  public sent: boolean = false;

  @ViewChild(NgForm, { static: true })
  public form: NgForm;

  public forgotPasswordRequest: ForgotPasswordRequest = new ForgotPasswordRequest();

  public constructor(private messageService: MessageService,
                     private validationService: ValidationService) {
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
      this.sent = true;
    }
  }
}

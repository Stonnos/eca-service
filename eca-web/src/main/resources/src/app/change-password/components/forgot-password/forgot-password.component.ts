import { Component, OnInit } from '@angular/core';
import { MessageService } from "primeng/api";
import {BaseCreateDialogComponent} from "../../../common/dialog/base-create-dialog.component";
import {ValidationService} from "../../../common/services/validation.service";

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent extends BaseCreateDialogComponent<string> implements OnInit {

  public loading: boolean = false;

  public constructor(private messageService: MessageService,
                     private validationService: ValidationService) {
    super();
  }

  public ngOnInit(): void {
  }
}

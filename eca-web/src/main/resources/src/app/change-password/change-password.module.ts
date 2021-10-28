import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ResetPasswordService } from "./services/reset-password.service";
import { ResetPasswordComponent } from "./components/reset-password/reset-password.component";
import { SharedModule } from "../common/shared.module";
import { ChangePasswordDialogComponent } from "./components/change-password-dialog/change-password-dialog.component";
import { ChangePasswordService } from "./services/change-password.service";
import { ConfirmChangePasswordComponent } from "./components/confirm-change-password/confirm-change-password.component";
import { CreateResetPasswordRequestComponent } from "./components/create-reset-password-request/create-reset-password-request.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    CreateResetPasswordRequestComponent,
    ResetPasswordComponent,
    ChangePasswordDialogComponent,
    ConfirmChangePasswordComponent
  ],
  exports: [
    ChangePasswordDialogComponent
  ],
  providers: [
    ResetPasswordService,
    ChangePasswordService
  ]
})
export class ChangePasswordModule {
}

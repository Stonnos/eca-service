import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ForgotPasswordComponent } from "./components/forgot-password/forgot-password.component";
import { ResetPasswordService } from "./services/reset-password.service";
import { ResetPasswordComponent } from "./components/reset-password/reset-password.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    ForgotPasswordComponent,
    ResetPasswordComponent
  ],
  providers: [
    ResetPasswordService
  ]
})
export class ChangePasswordModule {
}

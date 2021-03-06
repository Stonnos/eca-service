import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { UserProfileComponent } from "./components/user-profile.component";
import { ChangePasswordModule } from "../change-password/change-password.module";
import { UpdateUserEmailModule } from "../update-user-email/update-user-email.module";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
    ChangePasswordModule,
    UpdateUserEmailModule
  ],
  declarations: [
    UserProfileComponent
  ],
  exports: [
    UserProfileComponent
  ]
})
export class UserProfileModule {
}

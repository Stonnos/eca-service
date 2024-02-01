import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { UserProfileNotificationOptionsComponent } from "./components/user-profile-notification-options.component";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    UserProfileNotificationOptionsComponent
  ],
  exports: [
    UserProfileNotificationOptionsComponent
  ]
})
export class UserProfileNotificationOptionsModule {
}

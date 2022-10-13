import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { FilterModule } from "../filter/filter.module";
import { SharedModule } from "../common/shared.module";
import { NotificationsCenterComponent } from "./components/notifications-center.component";
import { UserNotificationsService } from "./services/user-notifications.service";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
    FilterModule
  ],
  declarations: [
    NotificationsCenterComponent
  ],
  exports: [
    NotificationsCenterComponent
  ],
  providers: [
    UserNotificationsService
  ]
})
export class NotificationsCenterModule {
}

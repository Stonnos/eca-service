import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { UserProfileTabsComponent } from "./components/user-profile-tabs.component";
import { UserProfileModule } from "../user-profile/user-profile.module";
import { UserProfileNotificationOptionsModule } from "../user-profile-notification-options/user-profile-notification-options.module";
import { PersonalAccessTokensModule } from '../personal-access-tokens/personal-access-tokens.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    UserProfileModule,
    UserProfileNotificationOptionsModule,
    PersonalAccessTokensModule
  ],
  declarations: [
    UserProfileTabsComponent
  ]
})
export class UserProfileTabsModule {
}

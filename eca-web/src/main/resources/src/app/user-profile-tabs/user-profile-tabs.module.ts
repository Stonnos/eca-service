import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { UserProfileTabsComponent } from "./components/user-profile-tabs.component";
import { UserProfileModule } from "../user-profile/user-profile.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    UserProfileModule
  ],
  declarations: [
    UserProfileTabsComponent
  ]
})
export class UserProfileTabsModule {
}

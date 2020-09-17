import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { UserProfileComponent } from "./components/user-profile.component";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule
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

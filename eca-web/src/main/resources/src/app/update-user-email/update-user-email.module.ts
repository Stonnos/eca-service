import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { UpdateUserEmailComponent } from "./components/change-email-dialog/update-user-email.component";
import { ChangeEmailService } from "./services/change-email.service";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    UpdateUserEmailComponent
  ],
  exports: [
    UpdateUserEmailComponent
  ],
  providers: [
    ChangeEmailService
  ]
})
export class UpdateUserEmailModule {
}

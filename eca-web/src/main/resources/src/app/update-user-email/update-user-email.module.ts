import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { UpdateUserEmailComponent } from "./components/change-email-dialog/update-user-email.component";
import { ChangeEmailService } from "./services/change-email.service";
import { ConfirmChangeEmailComponent } from "./components/confirm-change-email/confirm-change-email.component";
import {SharedModule} from "../common/shared.module";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        UiComponentsModule,
        SharedModule
    ],
  declarations: [
    UpdateUserEmailComponent,
    ConfirmChangeEmailComponent
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

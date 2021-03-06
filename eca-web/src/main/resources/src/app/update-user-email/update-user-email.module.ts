import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { UpdateUserEmailComponent } from "./components/update-user-email.component";

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
  providers: []
})
export class UpdateUserEmailModule {
}

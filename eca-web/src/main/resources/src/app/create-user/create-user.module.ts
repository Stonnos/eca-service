import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { CreateUserComponent } from "./components/create-user.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    CreateUserComponent
  ],
  exports: [
    CreateUserComponent
  ],
  providers: []
})
export class CreateUserModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { CreateClassifierComponent } from "./components/create-classifier.component";
import { SharedModule } from "../common/shared.module";
import { FormTemplatesModule } from "../form-templates/form-templates.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule,
    FormTemplatesModule
  ],
  declarations: [
    CreateClassifierComponent
  ],
  exports: [
    CreateClassifierComponent
  ],
  providers: []
})
export class CreateClassifierModule {
}

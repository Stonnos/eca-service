import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { FormTemplateDialogComponent } from "./components/form-template-dialog.component";
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
    FormTemplateDialogComponent
  ],
  exports: [
    FormTemplateDialogComponent
  ],
  providers: [
  ]
})
export class FormTemplatesDialogModule {
}

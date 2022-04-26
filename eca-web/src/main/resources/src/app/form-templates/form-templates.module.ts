import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { FormTemplatesService } from "./services/form-templates.service";
import { FormTemplateComponent } from "./components/form-template.component";
import { SharedModule } from "../common/shared.module";
import { FormTemplatesMapper } from "./services/form-templates.mapper";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    FormTemplateComponent
  ],
  exports: [
    FormTemplateComponent
  ],
  providers: [
    FormTemplatesService,
    FormTemplatesMapper
  ]
})
export class FormTemplatesModule {
}

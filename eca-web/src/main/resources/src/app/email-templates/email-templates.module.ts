import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { FilterModule } from "../filter/filter.module";
import { SharedModule } from "../common/shared.module";
import { TemplatesListComponent } from "./components/templates-list/templates-list.component";
import { EmailTemplatesService } from "./services/email-templates.service";
import { TemplateDetailsComponent } from "./components/template-details/template-details.component";
import { TemplateParametersListComponent } from "./components/template-parameters-list/template-parameters-list.component";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
    FilterModule
  ],
  declarations: [
    TemplatesListComponent,
    TemplateDetailsComponent,
    TemplateParametersListComponent
  ],
  providers: [
    EmailTemplatesService
  ]
})
export class EmailTemplatesModule {
}

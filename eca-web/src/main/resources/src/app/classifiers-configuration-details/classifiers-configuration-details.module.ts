import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassifierOptionsService } from "./services/classifier-options.service";
import { ClassifiersConfigurationDetailsComponent } from "./components/classifiers-configuration-details.component";
import { ClassifiersConfigurationMenuModule } from "../classifiers-configuration-menu/classifiers-configuration-menu.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    ClassifiersConfigurationMenuModule
  ],
  declarations: [
    ClassifiersConfigurationDetailsComponent
  ],
  exports: [
    ClassifiersConfigurationDetailsComponent
  ],
  providers: [
    ClassifierOptionsService
  ]
})
export class ClassifiersConfigurationDetailsModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassifiersConfigurationsComponent } from "./components/classifiers-configurations.component";
import { ClassifiersConfigurationsService } from "./services/classifiers-configurations.service";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    ClassifiersConfigurationsComponent
  ],
  exports: [
    ClassifiersConfigurationsComponent
  ],
  providers: [
    ClassifiersConfigurationsService
  ]
})
export class ClassifiersConfigurationsModule {
}

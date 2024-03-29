import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassifiersConfigurationDetailsModule } from "../classifiers-configuration-details/classifiers-configuration-details.module";
import { ClassifiersConfigurationFullDetailsComponent } from "./components/classifiers-configuration-full-details.component";
import { ClassifiersConfigurationHistoryModule } from "../classifiers-configuration-history/classifiers-configuration-history.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    ClassifiersConfigurationDetailsModule,
    ClassifiersConfigurationHistoryModule
  ],
  declarations: [
    ClassifiersConfigurationFullDetailsComponent
  ]
})
export class ClassifiersConfigurationFullDetailsModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassifiersConfigurationMenuComponent } from "./components/classifiers-configuration-menu.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    ClassifiersConfigurationMenuComponent
  ],
  exports: [
    ClassifiersConfigurationMenuComponent
  ],
  providers: []
})
export class ClassifiersConfigurationMenuModule {
}

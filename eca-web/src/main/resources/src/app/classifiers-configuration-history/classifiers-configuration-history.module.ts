import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassifiersConfigurationHistoryComponent } from "./components/classifiers-configuration-history.component";
import { SharedModule } from "../common/shared.module";
import { FilterModule } from "../filter/filter.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule,
    FilterModule
  ],
  declarations: [
    ClassifiersConfigurationHistoryComponent
  ],
  exports: [
    ClassifiersConfigurationHistoryComponent
  ]
})
export class ClassifiersConfigurationHistoryModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { FilterComponent } from "./components/filter.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    FilterComponent
  ],
  exports: [
    FilterComponent
  ],
  providers: []
})
export class FilterModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassificationCostsComponent } from "./components/classification-costs.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    ClassificationCostsComponent
  ],
  providers: []
})
export class ClassificationCostsModule {
}

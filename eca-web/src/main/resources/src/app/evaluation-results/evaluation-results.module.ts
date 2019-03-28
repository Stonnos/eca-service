import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { EvaluationResultsComponent } from "./components/evaluation-results.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    EvaluationResultsComponent
  ],
  exports: [
    EvaluationResultsComponent
  ],
  providers: []
})
export class EvaluationResultsModule {
}

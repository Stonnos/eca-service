import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { EvaluationLogDetailsComponent } from "./components/evaluation-log-details.component";
import { EvaluationResultsModule } from "../evaluation-results/evaluation-results.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    EvaluationResultsModule
  ],
  declarations: [
    EvaluationLogDetailsComponent
  ],
  exports: [
    EvaluationLogDetailsComponent
  ],
  providers: []
})
export class EvaluationLogDetailsModule {
}

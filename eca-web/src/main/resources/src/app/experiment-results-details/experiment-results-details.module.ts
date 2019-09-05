import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { EvaluationResultsModule } from "../evaluation-results/evaluation-results.module";
import { ExperimentResultsDetailsComponent } from "./components/experiment-results-details.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    EvaluationResultsModule
  ],
  declarations: [
    ExperimentResultsDetailsComponent
  ],
  exports: [
    ExperimentResultsDetailsComponent
  ],
  providers: []
})
export class ExperimentResultsDetailsModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { EvaluationResultsComponent } from "./components/evaluation-results.component";
import { ClassificationCostsModule } from "../classification-costs/classification-costs.module";
import { RocCurveChartModule } from "../roc-curve-chart/roc-curve-chart.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    ClassificationCostsModule,
    RocCurveChartModule
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

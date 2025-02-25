import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { EvaluationLogRocCurveComponent } from "./components/evaluation-log-roc-curve.component";
import { EvaluationResultsModule } from "../evaluation-results/evaluation-results.module";
import { SharedModule } from "../common/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule,
    EvaluationResultsModule
  ],
  declarations: [
    EvaluationLogRocCurveComponent
  ],
  exports: [
    EvaluationLogRocCurveComponent
  ],
  providers: []
})
export class EvaluationLogRocCurveModule {
}

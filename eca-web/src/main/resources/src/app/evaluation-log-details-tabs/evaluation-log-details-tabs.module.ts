import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { EvaluationLogDetailsTabsComponent } from "./components/evaluation-log-details-tabs.component";
import { EvaluationLogDetailsModule } from '../evaluation-log-details/evaluation-log-details.module';
import { EvaluationLogRocCurveModule } from '../evaluation-log-roc-curve/evaluation-log-roc-curve.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    EvaluationLogDetailsModule,
    EvaluationLogRocCurveModule
  ],
  declarations: [
    EvaluationLogDetailsTabsComponent
  ]
})
export class EvaluationLogDetailsTabsModule {
}

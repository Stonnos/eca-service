import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { EvaluationLogDetailsComponent } from "./components/evaluation-log-details.component";
import { EvaluationResultsModule } from "../evaluation-results/evaluation-results.module";
import { SharedModule } from "../common/shared.module";
import { ClassifyInstanceModule } from '../classify-instance/classify-instance.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule,
    EvaluationResultsModule,
    ClassifyInstanceModule
  ],
  declarations: [
    EvaluationLogDetailsComponent
  ],
  providers: []
})
export class EvaluationLogDetailsModule {
}

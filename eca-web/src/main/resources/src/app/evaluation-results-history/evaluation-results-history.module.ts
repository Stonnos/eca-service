import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { FilterModule } from "../filter/filter.module";
import { SharedModule } from "../common/shared.module";
import { EvaluationResultsHistoryComponent } from "./components/evaluation-results-history.component";
import { EvaluationResultsHistoryService } from "./services/evaluation-results-history.service";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
    FilterModule
  ],
  declarations: [
    EvaluationResultsHistoryComponent
  ],
  providers: [
    EvaluationResultsHistoryService
  ]
})
export class EvaluationResultsHistoryModule {
}

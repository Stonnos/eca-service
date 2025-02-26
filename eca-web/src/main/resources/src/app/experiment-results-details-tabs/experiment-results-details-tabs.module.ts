import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ExperimentResultsDetailsTabsComponent } from "./components/experiment-results-details-tabs.component";
import { ExperimentResultsRocCurveModule } from '../experiment-results-roc-curve/experiment-results-roc-curve.module';
import { ExperimentResultsDetailsModule } from '../experiment-results-details/experiment-results-details.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    ExperimentResultsRocCurveModule,
    ExperimentResultsDetailsModule
  ],
  declarations: [
    ExperimentResultsDetailsTabsComponent
  ]
})
export class ExperimentResultsDetailsTabsModule {
}

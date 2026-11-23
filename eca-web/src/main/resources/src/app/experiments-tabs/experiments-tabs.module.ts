import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ExperimentStatisticsModule } from "../experiment-statistics/experiment-statistics.module";
import { ExperimentTabsComponent } from "./components/experiment-tabs.component";
import { ExperimentsModule } from "../experiments/experiments.module";
import { ExperimentTimeSeriesStatisticsModule } from '../experiment-time-series-statistics/experiment-time-series-statistics.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    ExperimentStatisticsModule,
    ExperimentTimeSeriesStatisticsModule,
    ExperimentsModule
  ],
  declarations: [
    ExperimentTabsComponent
  ]
})
export class ExperimentsTabsModule {
}

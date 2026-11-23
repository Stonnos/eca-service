import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { ExperimentTimeSeriesStatisticsComponent } from "./components/experiment-time-series-statistics.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    ExperimentTimeSeriesStatisticsComponent
  ],
  exports: [
    ExperimentTimeSeriesStatisticsComponent
  ],
  providers: []
})
export class ExperimentTimeSeriesStatisticsModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ExperimentListComponent } from "./components/experiment-list.component";
import { UiComponentsModule } from "../ui-components.module";
import { ExperimentsService } from "./services/experiments.service";
import { FilterModule } from "../filter/filter.module";
import { RequestStatusesStatisticsModule } from "../request-statuses-statistics/request-statuses-statistics.module";
import { ErsReportModule } from "../ers-report/ers-report.module";
import { CreateExperimentModule } from "../create-experiment/create-experiment.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    FilterModule,
    RequestStatusesStatisticsModule,
    ErsReportModule,
    CreateExperimentModule
  ],
  declarations: [
    ExperimentListComponent
  ],
  exports: [
    ExperimentListComponent
  ],
  providers: [
    ExperimentsService
  ]
})
export class ExperimentsModule {
}

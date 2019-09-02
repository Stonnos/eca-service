import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ExperimentListComponent } from "./components/experiment-list.component";
import { UiComponentsModule } from "../ui-components.module";
import { ExperimentsService } from "./services/experiments.service";
import { FilterModule } from "../filter/filter.module";
import { RequestStatusesStatisticsModule } from "../request-statuses-statistics/request-statuses-statistics.module";
import { CreateExperimentModule } from "../create-experiment/create-experiment.module";
import { SharedModule } from "../common/shared.module";
import { ExperimentErsReportModule } from "../experiment-ers-report/experiment-ers-report.module";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
    FilterModule,
    RequestStatusesStatisticsModule,
    ExperimentErsReportModule,
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

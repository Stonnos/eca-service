import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ExperimentStatisticsComponent } from "./components/experiment-statistics.component";
import { SharedModule } from "../common/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    ExperimentStatisticsComponent
  ],
  exports: [
    ExperimentStatisticsComponent
  ],
  providers: []
})
export class ExperimentStatisticsModule {
}

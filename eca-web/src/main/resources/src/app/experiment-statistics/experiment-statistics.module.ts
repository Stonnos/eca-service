import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ExperimentStatisticsComponent } from "./components/experiment-statistics.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
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

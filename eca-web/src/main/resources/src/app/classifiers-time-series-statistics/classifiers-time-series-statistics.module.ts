import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { ClassifiersTimeSeriesStatisticsComponent } from "./components/classifiers-time-series-statistics.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    ClassifiersTimeSeriesStatisticsComponent
  ],
  exports: [
    ClassifiersTimeSeriesStatisticsComponent
  ],
  providers: []
})
export class ClassifiersTimeSeriesStatisticsModule {
}

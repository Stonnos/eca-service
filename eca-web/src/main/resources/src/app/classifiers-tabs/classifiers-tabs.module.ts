import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassifiersModule } from "../classifiers/classifiers.module";
import { ClassifiersStatisticsModule } from "../classifiers-statistics/classifiers-statistics.module";
import { ClassifiersTabsComponent } from "./components/classifiers-tabs.component";
import { ClassifiersTimeSeriesStatisticsModule } from '../classifiers-time-series-statistics/classifiers-time-series-statistics.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    ClassifiersModule,
    ClassifiersStatisticsModule,
    ClassifiersTimeSeriesStatisticsModule
  ],
  declarations: [
    ClassifiersTabsComponent
  ]
})
export class ClassifiersTabsModule {
}

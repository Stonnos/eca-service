import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ExperimentStatisticsModule } from "../experiment-statistics/experiment-statistics.module";
import { ClassifierOptionsModule } from "../classifier-options/classifier-options.module";
import { ExperimentTabsComponent } from "./components/experiment-tabs.component";
import { ExperimentsModule } from "../experiments/experiments.module";
import { ClassifiersConfigurationsModule } from "../classifiers-configurations/classifiers-configurations.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    ExperimentStatisticsModule,
    ClassifierOptionsModule,
    ClassifiersConfigurationsModule,
    ExperimentsModule
  ],
  declarations: [
    ExperimentTabsComponent
  ]
})
export class ExperimentsTabsModule {
}

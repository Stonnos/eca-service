import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassifiersModule } from "../classifiers/classifiers.module";
import { ClassifiersStatisticsModule } from "../classifiers-statistics/classifiers-statistics.module";
import { ClassifiersTabsComponent } from "./components/classifiers-tabs.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    ClassifiersModule,
    ClassifiersStatisticsModule,
  ],
  declarations: [
    ClassifiersTabsComponent
  ]
})
export class ClassifiersTabsModule {
}

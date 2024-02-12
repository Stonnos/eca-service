import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { InstancesDetailsTabsComponent } from "./components/instances-details-tabs.component";
import { InstancesDetailsModule } from "../instances-details/instances-details.module";
import { InstancesStatisticsModule } from "../instances-statistics/instances-statistics.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    InstancesDetailsModule,
    InstancesStatisticsModule
  ],
  declarations: [
    InstancesDetailsTabsComponent
  ]
})
export class InstancesDetailsTabsModule {
}

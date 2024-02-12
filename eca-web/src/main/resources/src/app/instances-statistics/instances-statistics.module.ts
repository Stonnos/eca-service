import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { InstancesStatisticsComponent } from "./components/instances-statistics.component";
import { CreateEditInstancesModule } from "../create-edit-instances/create-edit-instances.module";
import { ExportInstancesModule } from "../export-instances/export-instances.module";
import { AttributesModule } from "../attributes/attributes.module";
import {
  NominalAttributeStatisticsTableComponent
} from "./components/nominal-attribute-statistics-table/nominal-attribute-statistics-table.component";
import {
  NumericAttributeStatisticsTableComponent
} from "./components/numeric-attribute-statistics-table/numeric-attribute-statistics-table.component";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
    CreateEditInstancesModule,
    ExportInstancesModule,
    AttributesModule
  ],
  exports: [
    InstancesStatisticsComponent
  ],
  declarations: [
    InstancesStatisticsComponent,
    NominalAttributeStatisticsTableComponent,
    NumericAttributeStatisticsTableComponent
  ]
})
export class InstancesStatisticsModule {
}

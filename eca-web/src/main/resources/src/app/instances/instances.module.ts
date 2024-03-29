import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { InstancesListComponent } from "./components/instances-list.component";
import { InstancesService } from "./services/instances.service";
import { CreateEditInstancesModule } from "../create-edit-instances/create-edit-instances.module";
import { ExportInstancesModule } from "../export-instances/export-instances.module";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
    CreateEditInstancesModule,
    ExportInstancesModule
  ],
  declarations: [
    InstancesListComponent
  ],
  exports: [
    InstancesListComponent
  ],
  providers: [
    InstancesService
  ]
})
export class InstancesModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { InstancesDetailsComponent } from "./components/instances-details.component";
import { CreateEditInstancesModule } from "../create-edit-instances/create-edit-instances.module";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
    CreateEditInstancesModule
  ],
  declarations: [
    InstancesDetailsComponent
  ]
})
export class InstancesDetailsModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { InstancesInfoDetailsComponent } from "./components/instances-info-details.component";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    InstancesInfoDetailsComponent
  ],
  exports: [
    InstancesInfoDetailsComponent
  ]
})
export class InstancesInfoDetailsModule {
}

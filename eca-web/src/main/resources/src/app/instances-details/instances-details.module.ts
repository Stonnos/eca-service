import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { InstancesDetailsComponent } from "./components/instances-details.component";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    InstancesDetailsComponent
  ]
})
export class InstancesDetailsModule {
}

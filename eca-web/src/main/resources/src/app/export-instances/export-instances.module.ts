import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { ExportInstancesComponent } from "./components/export-instances.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    ExportInstancesComponent
  ],
  exports: [
    ExportInstancesComponent
  ],
  providers: []
})
export class ExportInstancesModule {
}

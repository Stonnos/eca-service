import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { AttributesScatterPlotDialog } from "./components/attributes-scatter-plot-dialog";
import { SharedModule } from "../common/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    AttributesScatterPlotDialog
  ],
  exports: [
    AttributesScatterPlotDialog
  ],
  providers: []
})
export class AttributesScatterPlotDialogModule {
}

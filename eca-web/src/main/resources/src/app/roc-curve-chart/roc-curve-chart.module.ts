import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { RocCurveChartComponent } from "./components/roc-curve-chart.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    RocCurveChartComponent
  ],
  exports: [
    RocCurveChartComponent
  ],
  providers: []
})
export class RocCurveChartModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ExperimentErsReportComponent } from "./components/experiment-ers-report.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    ExperimentErsReportComponent
  ],
  exports: [
    ExperimentErsReportComponent
  ],
  providers: []
})
export class ExperimentErsReportModule {
}

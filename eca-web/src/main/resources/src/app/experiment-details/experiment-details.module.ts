import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ExperimentDetailsComponent } from "./components/experiment-details.component";
import { ExperimentErsReportModule } from "../experiment-ers-report/experiment-ers-report.module";
import { SharedModule } from "../common/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule,
    ExperimentErsReportModule
  ],
  declarations: [
    ExperimentDetailsComponent
  ],
  exports: [
    ExperimentDetailsComponent
  ],
  providers: []
})
export class ExperimentDetailsModule {
}

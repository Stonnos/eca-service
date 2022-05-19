import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ExperimentErsReportComponent } from "./components/experiment-ers-report.component";
import { SharedModule } from "../common/shared.module";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        UiComponentsModule,
        SharedModule
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

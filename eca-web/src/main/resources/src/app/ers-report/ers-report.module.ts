import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ErsReportComponent } from "./components/ers-report.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    ErsReportComponent
  ],
  exports: [
    ErsReportComponent
  ],
  providers: []
})
export class ErsReportModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ContingencyTableDialog } from "./components/contingency-table-dialog";
import { SharedModule } from "../common/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    ContingencyTableDialog
  ],
  exports: [
    ContingencyTableDialog
  ],
  providers: []
})
export class ContingencyTableDialogModule {
}

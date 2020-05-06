import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { UploadClassifierOptionsDialogComponent } from "./components/upload-classifier-options-dialog.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    UploadClassifierOptionsDialogComponent
  ],
  exports: [
    UploadClassifierOptionsDialogComponent
  ],
  providers: []
})
export class UploadClassifierOptionsDialogModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { PageRequestService } from "./services/page-request.service";
import { FieldService } from "./services/field.service";
import { ReportsService } from "./services/report.service";
import { RequestStatusComponent } from "./request-status/components/request-status.component";
import { ValidationService } from "./services/validation.service";
import { EqualValidator } from "./validators/equal-validator.directive";
import { SearchQueryComponent } from "./search-query/search-query.component";
import { UploadTrainingDataComponent } from "./upload-training-data/upload-training-data.component";
import { ConfirmDialogComponent } from "./confirm-dialog/confirm-dialog.component";
import { EditableInputTextComponent } from "./editable-input-text/editable-input-text.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    RequestStatusComponent,
    SearchQueryComponent,
    UploadTrainingDataComponent,
    EqualValidator,
    ConfirmDialogComponent,
    EditableInputTextComponent,
  ],
  exports: [
    RequestStatusComponent,
    SearchQueryComponent,
    UploadTrainingDataComponent,
    EqualValidator,
    ConfirmDialogComponent,
    EditableInputTextComponent
  ],
  providers: [
    PageRequestService,
    FieldService,
    ReportsService,
    ValidationService
  ]
})
export class SharedModule {
}

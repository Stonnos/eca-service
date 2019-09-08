import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { PageRequestService } from "./services/page-request.service";
import { FieldService } from "./services/field.service";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  providers: [
    PageRequestService,
    FieldService
  ]
})
export class SharedModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassifierOptionsRequestsComponent } from "./components/classifier-options-requests.component";
import { ClassifierOptionsRequestService } from "./services/classifier-options-request.service";
import { FilterModule } from "../filter/filter.module";
import { SharedModule } from "../common/shared.module";

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    UiComponentsModule,
    FilterModule
  ],
  declarations: [
    ClassifierOptionsRequestsComponent
  ],
  providers: [
    ClassifierOptionsRequestService
  ]
})
export class ClassifierOptionsRequestsModule {
}

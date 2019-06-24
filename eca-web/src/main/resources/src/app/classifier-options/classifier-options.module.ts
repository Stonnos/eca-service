import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassifierOptionsService } from "./services/classifier-options.service";
import { ClassifierOptionsComponent } from "./components/classifier-options.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    ClassifierOptionsComponent
  ],
  exports: [
    ClassifierOptionsComponent
  ],
  providers: [
    ClassifierOptionsService
  ]
})
export class ClassifierOptionsModule {
}

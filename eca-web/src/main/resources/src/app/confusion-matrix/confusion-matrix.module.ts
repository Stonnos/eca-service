import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ConfusionMatrixComponent } from "./components/confusion-matrix.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    ConfusionMatrixComponent
  ],
  exports: [
    ConfusionMatrixComponent
  ],
  providers: []
})
export class ConfusionMatrixModule {
}

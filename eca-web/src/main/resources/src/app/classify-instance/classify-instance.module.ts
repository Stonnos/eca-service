import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { ClassifyInstanceComponent } from './components/classify-instance.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    ClassifyInstanceComponent
  ],
  exports: [
    ClassifyInstanceComponent
  ],
  providers: []
})
export class ClassifyInstanceModule {
}

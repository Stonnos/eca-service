import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ClassifierListComponent } from "./components/classifier-list.component";
import { UiComponentsModule } from "../ui-components.module";
import { ClassifiersService } from "./services/classifiers.service";
import { FilterModule } from "../filter/filter.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    FilterModule
  ],
  declarations: [
    ClassifierListComponent
  ],
  providers: [
    ClassifiersService
  ]
})
export class ClassifiersModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ClassifierListComponent } from "./components/classifier-list.component";
import { UiComponentsModule } from "../ui-components.module";
import { ClassifiersService } from "./services/classifiers.service";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
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

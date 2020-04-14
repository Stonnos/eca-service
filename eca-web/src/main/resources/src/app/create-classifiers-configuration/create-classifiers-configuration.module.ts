import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { CreateClassifiersConfigurationComponent } from "./components/create-classifiers-configuration.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    CreateClassifiersConfigurationComponent
  ],
  exports: [
    CreateClassifiersConfigurationComponent
  ],
  providers: []
})
export class CreateClassifiersConfigurationModule {
}

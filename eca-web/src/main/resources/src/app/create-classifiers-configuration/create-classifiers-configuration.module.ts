import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { CreateClassifiersConfigurationComponent } from "./components/create-classifiers-configuration.component";
import { SharedModule } from "../common/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
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

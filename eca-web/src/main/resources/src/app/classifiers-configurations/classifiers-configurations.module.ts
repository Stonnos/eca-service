import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassifiersConfigurationsComponent } from "./components/classifiers-configurations.component";
import { ClassifiersConfigurationsService } from "./services/classifiers-configurations.service";
import { CreateClassifiersConfigurationModule } from "../create-classifiers-configuration/create-classifiers-configuration.module";
import { UploadClassifierOptionsDialogModule } from "../upload-classifier-options-dialog/upload-classifier-options-dialog.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    CreateClassifiersConfigurationModule,
    UploadClassifierOptionsDialogModule
  ],
  declarations: [
    ClassifiersConfigurationsComponent
  ],
  exports: [
    ClassifiersConfigurationsComponent
  ],
  providers: [
    ClassifiersConfigurationsService
  ]
})
export class ClassifiersConfigurationsModule {
}

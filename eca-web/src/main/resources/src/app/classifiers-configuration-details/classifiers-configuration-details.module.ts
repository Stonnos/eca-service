import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { ClassifierOptionsService } from "./services/classifier-options.service";
import { ClassifiersConfigurationDetailsComponent } from "./components/classifiers-configuration-details.component";
import { ClassifiersConfigurationMenuModule } from "../classifiers-configuration-menu/classifiers-configuration-menu.module";
import { CreateClassifiersConfigurationModule } from "../create-classifiers-configuration/create-classifiers-configuration.module";
import { UploadClassifierOptionsDialogModule } from "../upload-classifier-options-dialog/upload-classifier-options-dialog.module";
import { SharedModule } from "../common/shared.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    ClassifiersConfigurationMenuModule,
    CreateClassifiersConfigurationModule,
    UploadClassifierOptionsDialogModule,
    SharedModule
  ],
  declarations: [
    ClassifiersConfigurationDetailsComponent
  ],
  exports: [
    ClassifiersConfigurationDetailsComponent
  ],
  providers: [
    ClassifierOptionsService
  ]
})
export class ClassifiersConfigurationDetailsModule {
}

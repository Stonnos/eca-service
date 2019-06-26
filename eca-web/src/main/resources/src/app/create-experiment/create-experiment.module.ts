import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { CreateExperimentComponent } from "./components/create-experiment.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    CreateExperimentComponent
  ],
  exports: [
    CreateExperimentComponent
  ],
  providers: []
})
export class CreateExperimentModule {
}

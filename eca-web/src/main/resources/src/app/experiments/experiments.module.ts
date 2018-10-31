import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ExperimentListComponent } from "./components/experiment-list.component";
import { UiComponentsModule } from "../ui-components.module";
import { ExperimentsService } from "./services/experiments.service";
import { FilterModule } from "../filter/filter.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    FilterModule
  ],
  declarations: [
    ExperimentListComponent
  ],
  providers: [
    ExperimentsService
  ]
})
export class ExperimentsModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { SharedModule } from "../common/shared.module";
import { CreateEditInstancesComponent } from "./components/create-edit-instances.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule,
    SharedModule
  ],
  declarations: [
    CreateEditInstancesComponent
  ],
  exports: [
    CreateEditInstancesComponent
  ],
  providers: []
})
export class CreateEditInstancesModule {
}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UiComponentsModule } from "../ui-components.module";
import { RequestStatusesStatisticsComponent } from "./components/request-statuses-statistics.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UiComponentsModule
  ],
  declarations: [
    RequestStatusesStatisticsComponent
  ],
  exports: [
    RequestStatusesStatisticsComponent
  ],
  providers: []
})
export class RequestStatusesStatisticsModule {
}

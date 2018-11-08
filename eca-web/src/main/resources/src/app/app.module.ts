import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { AppRoutingModule } from "./app-routing.module";
import { DashboardComponent } from "./dashboard/dashboard.component";
import { ClassifiersModule } from "./classifiers/classifiers.module";
import { ExperimentsModule } from "./experiments/experiments.module";
import { UiComponentsModule } from "./ui-components.module";
import { HttpClientModule } from "@angular/common/http";
import { FormsModule } from "@angular/forms";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { FilterModule } from "./filter/filter.module";
import { ClassifierOptionsModule } from "./classifier-options/classifier-options.module";
import { RequestStatusesStatisticsModule } from "./request-statuses-statistics/request-statuses-statistics.module";
import { ClassifierOptionsRequestsModule } from "./classifier-options-requests/classifier-options-requests.module";

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    ClassifiersModule,
    ExperimentsModule,
    ClassifierOptionsModule,
    ClassifierOptionsRequestsModule,
    RequestStatusesStatisticsModule,
    FilterModule,
    UiComponentsModule,
    HttpClientModule,
    NoopAnimationsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

import { BrowserModule } from '@angular/platform-browser';
import { APP_INITIALIZER, NgModule } from '@angular/core';

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
import { ConfigService } from "./config.service";
import { CookieService } from "ngx-cookie-service";
import { AuthModule } from "./auth/auth.module";
import { ErsReportModule } from "./ers-report/ers-report.module";
import { ExperimentStatisticsModule } from "./experiment-statistics/experiment-statistics.module";
import { EvaluationResultsModule } from "./evaluation-results/evaluation-results.module";
import { ClassificationCostsModule } from "./classification-costs/classification-costs.module";

export function initializeApp(configService: ConfigService) {
  return () => configService.getConfigs();
}

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
    ErsReportModule,
    EvaluationResultsModule,
    ClassificationCostsModule,
    AuthModule,
    ExperimentStatisticsModule,
    UiComponentsModule,
    HttpClientModule,
    NoopAnimationsModule,
  ],
  providers: [
    ConfigService,
    CookieService,
    { provide: APP_INITIALIZER, useFactory: initializeApp, deps: [ConfigService], multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

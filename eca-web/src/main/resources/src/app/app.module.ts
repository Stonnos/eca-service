import { BrowserModule } from '@angular/platform-browser';
import { APP_INITIALIZER, NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { AppRoutingModule } from "./routing/app-routing.module";
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
import { ExperimentStatisticsModule } from "./experiment-statistics/experiment-statistics.module";
import { EvaluationResultsModule } from "./evaluation-results/evaluation-results.module";
import { ClassificationCostsModule } from "./classification-costs/classification-costs.module";
import { CreateExperimentModule } from "./create-experiment/create-experiment.module";
import { ExperimentsTabsModule } from "./experiments-tabs/experiments-tabs.module";
import { SharedModule } from "./common/shared.module";
import { EvaluationLogDetailsModule } from "./evaluation-log-details/evaluation-log-details.module";
import { ExperimentErsReportModule } from "./experiment-ers-report/experiment-ers-report.module";
import { ExperimentDetailsModule } from "./experiment-details/experiment-details.module";
import { ExperimentResultsDetailsModule } from "./experiment-results-details/experiment-results-details.module";
import { ClassifiersConfigurationsModule } from "./classifiers-configurations/classifiers-configurations.module";
import { CreateClassifiersConfigurationModule } from "./create-classifiers-configuration/create-classifiers-configuration.module";

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
    SharedModule,
    ExperimentsTabsModule,
    ClassifiersModule,
    ExperimentsModule,
    ClassifierOptionsModule,
    ClassifiersConfigurationsModule,
    CreateClassifiersConfigurationModule,
    ClassifierOptionsRequestsModule,
    RequestStatusesStatisticsModule,
    FilterModule,
    ExperimentErsReportModule,
    EvaluationResultsModule,
    EvaluationLogDetailsModule,
    ExperimentResultsDetailsModule,
    ClassificationCostsModule,
    AuthModule,
    ExperimentStatisticsModule,
    CreateExperimentModule,
    ExperimentDetailsModule,
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

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

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
import { RequestStatusesStatisticsModule } from "./request-statuses-statistics/request-statuses-statistics.module";
import { ClassifierOptionsRequestsModule } from "./classifier-options-requests/classifier-options-requests.module";
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
import { UploadClassifierOptionsDialogModule } from "./upload-classifier-options-dialog/upload-classifier-options-dialog.module";
import { ClassifiersConfigurationMenuModule } from "./classifiers-configuration-menu/classifiers-configuration-menu.module";
import { ClassifiersConfigurationDetailsModule } from "./classifiers-configuration-details/classifiers-configuration-details.module";
import { UsersModule } from "./users/users.module";
import { CreateUserModule } from "./create-user/create-user.module";
import { ChangePasswordModule } from "./change-password/change-password.module";

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
    UsersModule,
    CreateUserModule,
    ClassifiersConfigurationsModule,
    ClassifiersConfigurationDetailsModule,
    ClassifiersConfigurationMenuModule,
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
    UploadClassifierOptionsDialogModule,
    ChangePasswordModule,
    UiComponentsModule,
    HttpClientModule,
    NoopAnimationsModule,
  ],
  providers: [
    CookieService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

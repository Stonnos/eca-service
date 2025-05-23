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
import { RequestStatusesStatisticsModule } from "./request-statuses-statistics/request-statuses-statistics.module";
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
import { InstancesModule } from "./instances/instances.module";
import { CreateEditInstancesModule } from "./create-edit-instances/create-edit-instances.module";
import { UserProfileModule } from "./user-profile/user-profile.module";
import { UpdateUserEmailModule } from "./update-user-email/update-user-email.module";
import { AccessDeniedModule } from "./access-denied/access-denied.module";
import { AuditLogsModule } from "./audit-logs/audit-logs.module";
import { EmailTemplatesModule } from "./email-templates/email-templates.module";
import { InstancesDetailsModule } from "./instances-details/instances-details.module";
import { ExportInstancesModule } from "./export-instances/export-instances.module";
import { ClassifiersConfigurationFullDetailsModule } from "./classifiers-configuration-full-details/classifiers-configuration-full-details.module";
import { ClassifiersConfigurationHistoryModule } from "./classifiers-configuration-history/classifiers-configuration-history.module";
import { EventHandler } from "./common/event/event.handler";
import { NotificationsCenterModule } from "./notifications-center/notifications-center.module";
import { ClassifiersStatisticsModule } from "./classifiers-statistics/classifiers-statistics.module";
import { ClassifiersTabsModule } from "./classifiers-tabs/classifiers-tabs.module";
import { AttributesModule } from "./attributes/attributes.module";
import { CreateClassifierModule } from "./create-classifier/create-classifier.module";
import { CreateOptimalClassifierModule } from "./create-optimal-classifier/create-optimal-classifier.module";
import { EvaluationResultsHistoryModule } from "./evaluation-results-history/evaluation-results-history.module";
import { UserProfileTabsModule } from "./user-profile-tabs/user-profile-tabs.module";
import { UserProfileNotificationOptionsModule } from "./user-profile-notification-options/user-profile-notification-options.module";
import { InstancesDetailsTabsModule } from "./instances-details-tabs/instances-details-tabs.module";
import { InstancesStatisticsModule } from "./instances-statistics/instances-statistics.module";
import { ConfusionMatrixModule } from './confusion-matrix/confusion-matrix.module';
import { ClassifyInstanceModule } from './classify-instance/classify-instance.module';

export function eventSubscribe(eventHandler: EventHandler) {
  return () => eventHandler.eventSubscribe();
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
    ClassifiersTabsModule,
    ClassifiersModule,
    ExperimentsModule,
    UsersModule,
    UserProfileModule,
    CreateUserModule,
    ClassifiersConfigurationsModule,
    ClassifiersConfigurationDetailsModule,
    ClassifiersConfigurationMenuModule,
    CreateClassifiersConfigurationModule,
    RequestStatusesStatisticsModule,
    FilterModule,
    ExperimentErsReportModule,
    EvaluationResultsModule,
    EvaluationLogDetailsModule,
    ExperimentResultsDetailsModule,
    ClassificationCostsModule,
    ConfusionMatrixModule,
    AuthModule,
    ExperimentStatisticsModule,
    CreateExperimentModule,
    CreateClassifierModule,
    CreateOptimalClassifierModule,
    ExperimentDetailsModule,
    UploadClassifierOptionsDialogModule,
    InstancesModule,
    CreateEditInstancesModule,
    ChangePasswordModule,
    UpdateUserEmailModule,
    UiComponentsModule,
    HttpClientModule,
    NoopAnimationsModule,
    AccessDeniedModule,
    AuditLogsModule,
    EmailTemplatesModule,
    InstancesDetailsModule,
    ExportInstancesModule,
    ClassifiersConfigurationHistoryModule,
    ClassifiersConfigurationFullDetailsModule,
    NotificationsCenterModule,
    ClassifiersStatisticsModule,
    AttributesModule,
    EvaluationResultsHistoryModule,
    UserProfileTabsModule,
    UserProfileNotificationOptionsModule,
    InstancesStatisticsModule,
    InstancesDetailsTabsModule,
    ClassifyInstanceModule
  ],
  providers: [
    CookieService,
    {
      provide: APP_INITIALIZER,
      useFactory: eventSubscribe,
      deps: [EventHandler],
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

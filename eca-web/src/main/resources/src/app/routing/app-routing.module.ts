import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from "../dashboard/dashboard.component";
import { LoginComponent } from "../auth/components/login.component";
import { ExperimentTabsComponent } from "../experiments-tabs/components/experiment-tabs.component";
import { ExperimentDetailsComponent } from "../experiment-details/components/experiment-details.component";
import { ExperimentResultsDetailsComponent } from "../experiment-results-details/components/experiment-results-details.component";
import { UsersListComponent } from "../users/components/users-list.component";
import { ResetPasswordComponent } from "../change-password/components/reset-password/reset-password.component";
import { InstancesListComponent } from "../instances/components/instances-list.component";
import { AccessDeniedComponent } from "../access-denied/components/access-denied.component";
import { AuditLogsComponent } from "../audit-logs/components/audit-logs.component";
import { TemplatesListComponent } from "../email-templates/components/templates-list/templates-list.component";
import { CreateResetPasswordRequestComponent } from "../change-password/components/create-reset-password-request/create-reset-password-request.component";
import { ClassifiersConfigurationFullDetailsComponent } from "../classifiers-configuration-full-details/components/classifiers-configuration-full-details.component";
import { ClassifiersTabsComponent } from "../classifiers-tabs/components/classifiers-tabs.component";
import { EvaluationResultsHistoryComponent } from "../evaluation-results-history/components/evaluation-results-history.component";
import { UserProfileTabsComponent } from "../user-profile-tabs/components/user-profile-tabs.component";
import { InstancesDetailsTabsComponent } from "../instances-details-tabs/components/instances-details-tabs.component";
import { EvaluationLogDetailsTabsComponent } from '../evaluation-log-details-tabs/components/evaluation-log-details-tabs.component';

const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,
    children: [
      {
        path: 'classifiers',
        component: ClassifiersTabsComponent
      },
      {
        path: 'experiments',
        component: ExperimentTabsComponent
      },
      {
        path: 'evaluation-results-history',
        component: EvaluationResultsHistoryComponent
      },
      {
        path: 'instances',
        component: InstancesListComponent
      },
      {
        path: 'instances/details/:id',
        component: InstancesDetailsTabsComponent
      },
      {
        path: 'users',
        component: UsersListComponent
      },
      {
        path: 'audit-logs',
        component: AuditLogsComponent
      },
      {
        path: 'email-templates',
        component: TemplatesListComponent
      },
      {
        path: 'classifiers/evaluation-results/:id',
        component: EvaluationLogDetailsTabsComponent
      },
      {
        path: 'experiments/details/:id',
        component: ExperimentDetailsComponent
      },
      {
        path: 'experiments/results/details/:id',
        component: ExperimentResultsDetailsComponent
      },
      {
        path: 'experiments/classifiers-configuration/details/:id',
        component: ClassifiersConfigurationFullDetailsComponent
      },
      {
        path: 'profile',
        component: UserProfileTabsComponent
      },
    ]
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'create-reset-password-request',
    component: CreateResetPasswordRequestComponent
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent
  },
  {
    path: 'access-denied',
    component: AccessDeniedComponent
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from "../dashboard/dashboard.component";
import { ClassifierListComponent } from "../classifiers/components/classifier-list.component";
import { ClassifierOptionsRequestsComponent } from "../classifier-options-requests/components/classifier-options-requests.component";
import { LoginComponent } from "../auth/components/login.component";
import { ExperimentTabsComponent } from "../experiments-tabs/components/experiment-tabs.component";
import { EvaluationLogDetailsComponent } from "../evaluation-log-details/components/evaluation-log-details.component";
import { ExperimentDetailsComponent } from "../experiment-details/components/experiment-details.component";
import { ExperimentResultsDetailsComponent } from "../experiment-results-details/components/experiment-results-details.component";
import { UsersListComponent } from "../users/components/users-list.component";
import { ResetPasswordComponent } from "../change-password/components/reset-password/reset-password.component";
import { InstancesListComponent } from "../instances/components/instances-list.component";
import { UserProfileComponent } from "../user-profile/components/user-profile.component";
import { ConfirmChangePasswordComponent } from "../change-password/components/confirm-change-password/confirm-change-password.component";
import { AccessDeniedComponent } from "../access-denied/components/access-denied.component";
import { AuditLogsComponent } from "../audit-logs/components/audit-logs.component";
import { ConfirmChangeEmailComponent } from "../update-user-email/components/confirm-change-email/confirm-change-email.component";
import { TemplatesListComponent } from "../email-templates/components/templates-list/templates-list.component";
import { InstancesDetailsComponent } from "../instances-details/components/instances-details.component";
import { CreateResetPasswordRequestComponent } from "../change-password/components/create-reset-password-request/create-reset-password-request.component";
import { ClassifiersConfigurationFullDetailsComponent } from "../classifiers-configuration-full-details/components/classifiers-configuration-full-details.component";

const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,
    children: [
      {
        path: 'classifiers',
        component: ClassifierListComponent
      },
      {
        path: 'experiments',
        component: ExperimentTabsComponent
      },
      {
        path: 'classifiers-options-requests',
        component: ClassifierOptionsRequestsComponent
      },
      {
        path: 'instances',
        component: InstancesListComponent
      },
      {
        path: 'instances/details/:id',
        component: InstancesDetailsComponent
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
        component: EvaluationLogDetailsComponent
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
        component: UserProfileComponent
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
    path: 'change-password',
    component: ConfirmChangePasswordComponent
  },
  {
    path: 'change-email',
    component: ConfirmChangeEmailComponent
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

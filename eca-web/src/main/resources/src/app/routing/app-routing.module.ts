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
import { ClassifiersConfigurationDetailsComponent } from "../classifiers-configuration-details/components/classifiers-configuration-details.component";
import { UsersListComponent } from "../users/components/users-list.component";
import { ForgotPasswordComponent } from "../change-password/components/forgot-password/forgot-password.component";
import { ResetPasswordComponent } from "../change-password/components/reset-password/reset-password.component";

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
        path: 'users',
        component: UsersListComponent
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
        component: ClassifiersConfigurationDetailsComponent
      }
    ]
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent
  },
  {
    path: 'reset-password/:token',
    component: ResetPasswordComponent
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

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from "./dashboard/dashboard.component";
import { ClassifierListComponent } from "./classifiers/components/classifier-list.component";
import { ClassifierOptionsRequestsComponent } from "./classifier-options-requests/components/classifier-options-requests.component";
import { LoginComponent } from "./auth/components/login.component";
import { ExperimentTabsComponent } from "./experiments-tabs/components/experiment-tabs.component";
import { EvaluationLogDetailsComponent } from "./evaluation-log-details/components/evaluation-log-details.component";
import { ExperimentDetailsComponent } from "./experiment-details/components/experiment-details.component";

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
        path: 'classifiers/evaluation-results/:id',
        component: EvaluationLogDetailsComponent
      },
      {
        path: 'experiments/details/:id',
        component: ExperimentDetailsComponent
      },
    ]
  },
  {
    path: 'login',
    component: LoginComponent
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

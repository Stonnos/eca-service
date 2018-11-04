import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from "./dashboard/dashboard.component";
import { ClassifierListComponent } from "./classifiers/components/classifier-list.component";
import { ExperimentListComponent } from "./experiments/components/experiment-list.component";
import { ClassifierOptionsComponent } from "./classifier-options/components/classifier-options.component";

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
        component: ExperimentListComponent
      },
      {
        path: 'classifiers-options',
        component: ClassifierOptionsComponent
      }
    ]
  },
  {
    path: '',
    redirectTo: '/dashboard/experiments',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { useHash: true })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

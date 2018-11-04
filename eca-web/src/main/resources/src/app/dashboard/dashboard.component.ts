import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  items: MenuItem[];

  public ngOnInit() {
    this.items = [
      {
        label: 'Experiments',
        routerLink: ['/dashboard/experiments'],
        routerLinkActiveOptions: 'ui-menuitem-active'
      },
      {
        label: 'Classifiers',
        routerLink: ['/dashboard/classifiers'],
        routerLinkActiveOptions: 'ui-menuitem-active'
      },
      {
        label: 'Classifiers options',
        routerLink: ['/dashboard/classifiers-options'],
        routerLinkActiveOptions: 'ui-menuitem-active'
      }
    ];
  }
}

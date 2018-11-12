import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { AuthService } from "../auth/services/auth.service";

@Component({
  selector: 'app-dashboard',
  providers: [AuthService],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  items: MenuItem[];

  constructor(private authService: AuthService) {
  }

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
      },
      {
        label: 'Classifiers options requests',
        routerLink: ['/dashboard/classifiers-options-requests'],
        routerLinkActiveOptions: 'ui-menuitem-active'
      }
    ];
  }

  public logout() {
    this.authService.logout();
  }
}

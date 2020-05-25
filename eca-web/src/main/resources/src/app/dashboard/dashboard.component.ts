import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { LogoutService } from "../auth/services/logout.service";
import { AuthenticationKeys } from "../auth/model/auth.keys";

@Component({
  selector: 'app-dashboard',
  providers: [ LogoutService ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  public items: MenuItem[];

  constructor(private logoutService: LogoutService) {
  }

  public ngOnInit() {
    this.items = [
      {
        label: 'Эксперименты',
        routerLink: ['/dashboard/experiments']
      },
      {
        label: 'Классификаторы',
        routerLink: ['/dashboard/classifiers']
      },
      {
        label: 'Оптимальные настройки классификаторов',
        routerLink: ['/dashboard/classifiers-options-requests']
      },
      {
        label: 'Пользователи',
        routerLink: ['/dashboard/users']
      }
    ];
  }

  public getUserLogin(): string {
    return localStorage.getItem(AuthenticationKeys.USER_NAME);
  }

  public logout() {
    this.logoutService.logout();
  }
}

import { Component, OnInit } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { AuthService } from "../auth/services/auth.service";
import { UserService } from "../auth/services/user.service";
import { UserDto } from "../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-dashboard',
  providers: [ AuthService, UserService ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  public items: MenuItem[];

  private currentUser: UserDto;

  constructor(private authService: AuthService,
              private userService: UserService,
              private messageService: MessageService) {
  }

  public ngOnInit() {
    this.getCurrentUser();
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

  public getUserLogin(): string {
    return this.currentUser && this.currentUser.login;
  }

  public getCurrentUser() {
    this.userService.getCurrentUser().subscribe((userDto: UserDto) => {
      this.currentUser = userDto;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    });
  }

  public logout() {
    this.authService.logout();
  }
}

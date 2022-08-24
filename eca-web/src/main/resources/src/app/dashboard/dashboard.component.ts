import { Component, OnInit } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { LogoutService } from "../auth/services/logout.service";
import { MenuItemDto, UserDto } from "../../../../../../target/generated-sources/typescript/eca-web-dto";
import { HttpErrorResponse } from "@angular/common/http";
import { UsersService } from "../users/services/users.service";
import { WebAppService } from "../common/services/web-app.service";
import { EventService } from "../common/event/event.service";
import { EventType } from "../common/event/event.type";

@Component({
  selector: 'app-dashboard',
  providers: [ LogoutService ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  private user: UserDto;

  public items: MenuItem[] = [];

  public userMenuItems: MenuItem[] = [];

  public constructor(private logoutService: LogoutService,
                     private usersService: UsersService,
                     private webAppService: WebAppService,
                     private messageService: MessageService,
                     private eventService: EventService) {
  }

  public ngOnInit() {
    this.getCurrentUser();
    this.getMenuItems();
    this.initUserMenu();
  }

  public getUserLogin(): string {
    return this.user && this.user.login;
  }

  public getCurrentUser(): void {
    this.usersService.getCurrentUser().subscribe({
      next: (user: UserDto) => {
        this.user = user;
        if (user.pushEnabled) {
          this.eventService.publishEvent(EventType.INIT_PUSH);
        }
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    })
  }

  public logout() {
    this.usersService.logoutRequest()
      .subscribe({
        next: () => {
          this.logoutService.logout();
        },
        error: (error) => {
          this.handleLogoutError(error);
        }
      });
  }

  private getMenuItems() {
    this.webAppService.getMenuItems().subscribe({
      next: (menuItems: MenuItemDto[]) => {
        this.items = menuItems.map((item: MenuItemDto) => {
          return { label: item.label, routerLink: [item.routerLink] }
        });
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    });
  }

  private handleLogoutError(error): void {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 401) {
        this.logoutService.logout();
      } else {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    } else {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    }
  }

  private initUserMenu(): void {
    this.userMenuItems = [
      {
        label: 'Настройки',
        icon: 'pi pi-cog',
        routerLink: ['/dashboard/profile']
      },
      {
        label: 'Выход',
        icon: 'pi pi-sign-out',
        command: () => {
          this.logout();
        }
      }
    ];
  }
}

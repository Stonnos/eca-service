import { Component, OnInit } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { LogoutService } from "../auth/services/logout.service";
import { MenuItemDto, UserDto } from "../../../../../../target/generated-sources/typescript/eca-web-dto";
import { HttpErrorResponse } from "@angular/common/http";
import { UsersService } from "../users/services/users.service";
import { WebAppService } from "../common/services/web-app.service";
import { EventService } from "../common/event/event.service";
import { EventType, NotificationDto } from "../common/event/event.type";

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

  public virtualNotifications: NotificationDto[] = [];
  public notifications: NotificationDto[] = [
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:01:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:02:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:03:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:04:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:05:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:06:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:07:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:08:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:09:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:10:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:11:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:12:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:13:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:14:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:15:55"
    },
    {
      messageText: "Поступила новая заявка на эксперимент 1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c",
      messageType: "EXPERIMENT_STATUS",
      createdAt: "2022-09-01 12:16:55"
    },
  ];

  private first: number = 0;
  private rows: number = 0;
  private total: number = 16;
  private pageSize = 5;

  public constructor(private logoutService: LogoutService,
                     private usersService: UsersService,
                     private webAppService: WebAppService,
                     private messageService: MessageService,
                     private eventService: EventService) {
  }

  hasContent(): boolean {
    return this.rows < this.total;
  }

  load() {
    this.first = this.rows;
    this.rows = this.rows + this.pageSize;
    console.log('Laze event ' + this.first);
    console.log('Laze event ' + this.rows);
    //load data of required page
    let loadedData: NotificationDto[] = this.notifications.slice(this.first, this.rows);
    console.log(loadedData);
    this.virtualNotifications.push(...loadedData);
    console.log(this.virtualNotifications);
  }

  public ngOnInit() {
    this.getCurrentUser();
    this.getMenuItems();
    this.initUserMenu();
  }

  public onShow() {
    this.load();
  }

  public onHide() {
    this.virtualNotifications = [];
    this.first = 0;
    this.rows = 0;
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

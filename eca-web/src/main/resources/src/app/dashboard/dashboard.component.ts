import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MenuItem, MessageService } from 'primeng/api';
import { LogoutService } from "../auth/services/logout.service";
import {
  UiPermissionsDto, PushRequestDto,
  UserDto,
  UserNotificationStatisticsDto,
  MenuItemDto
} from "../../../../../../target/generated-sources/typescript/eca-web-dto";
import { HttpErrorResponse } from "@angular/common/http";
import { UsersService } from "../users/services/users.service";
import { AuthService } from "../auth/services/auth.service";
import { WebAppService } from "../common/services/web-app.service";
import { EventService } from "../common/event/event.service";
import { EventType } from "../common/event/event.type";
import { NotificationsCenterComponent } from "../notifications-center/components/notifications-center.component";
import { UserNotificationsService } from "../notifications-center/services/user-notifications.service";
import { Logger } from "../common/util/logging";
import { Subscription } from "rxjs";
import { PushService } from "../common/push/push.service";

@Component({
  selector: 'app-dashboard',
  providers: [ LogoutService ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {

  private static readonly MAX_NOT_READ_NOTIFICATIONS_COUNT_TO_DISPLAY = 99;

  private user: UserDto;

  private userPushSubscription: Subscription;

  public items: MenuItem[] = [];

  public userMenuItems: MenuItem[] = [];

  public userNotificationsStatistics: UserNotificationStatisticsDto;

  @ViewChild(NotificationsCenterComponent, { static: true })
  public notificationsCenter: NotificationsCenterComponent;

  public constructor(private logoutService: LogoutService,
                     private usersService: UsersService,
                     private webAppService: WebAppService,
                     private messageService: MessageService,
                     private eventService: EventService,
                     private authService: AuthService,
                     private userNotificationsService: UserNotificationsService,
                     private pushService: PushService) {
  }

  public ngOnInit() {
    this.getCurrentUser();
    this.getNotificationsStatistics();
    this.getUiPermissions();
    this.initUserMenu();
    this.subscribeForUserPushes();
  }

  public ngOnDestroy(): void {
    this.unSubscribeUserPushes();
  }

  public getUserLogin(): string {
    return this.user && this.user.login;
  }

  public showNotifications(): void {
    this.notificationsCenter.loadNext();
  }

  public hideNotifications(): void {
    this.notificationsCenter.clear();
  }

  public getNotReadNotificationsCount() {
    if (this.userNotificationsStatistics.notReadCount > DashboardComponent.MAX_NOT_READ_NOTIFICATIONS_COUNT_TO_DISPLAY) {
      return `${DashboardComponent.MAX_NOT_READ_NOTIFICATIONS_COUNT_TO_DISPLAY}+`;
    }
    return this.userNotificationsStatistics.notReadCount;
  }

  public getCurrentUser(): void {
    this.usersService.getCurrentUser().subscribe({
      next: (user: UserDto) => {
        this.user = user;
        this.eventService.publishEvent(EventType.INIT_PUSH);
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    })
  }

  public logout() {
    this.authService.logoutRequest()
      .subscribe({
        next: () => {
          this.logoutService.logout();
        },
        error: (error) => {
          this.handleLogoutError(error);
        }
      });
  }

  public getNotificationsStatistics(): void {
    this.userNotificationsService.getNotificationsStatistics()
      .subscribe({
        next: (userNotificationStatistics: UserNotificationStatisticsDto) => {
          this.userNotificationsStatistics = userNotificationStatistics;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private getUiPermissions() {
    this.webAppService.getUiPermissions().subscribe({
      next: (uiPermissionsDto: UiPermissionsDto) => {
        this.items = uiPermissionsDto.menuItems.map((item: MenuItemDto) => {
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

  private subscribeForUserPushes(): void {
    if (!this.userPushSubscription) {
      Logger.debug(`Subscribe user pushes in dashboard`);
      const filterPredicate = (pushRequestDto: PushRequestDto) => pushRequestDto.pushType == 'USER_NOTIFICATION';
      this.userPushSubscription = this.pushService.pushMessageSubscribe(filterPredicate)
        .subscribe({
          next: (pushRequestDto: PushRequestDto) => {
            Logger.debug(`Updates notifications statistics after new push ${pushRequestDto.requestId}`);
            this.getNotificationsStatistics();
          },
          error: (error) => {
            this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
          }
        });
    }
  }

  private unSubscribeUserPushes(): void {
    if (this.userPushSubscription) {
      this.userPushSubscription.unsubscribe();
      this.userPushSubscription = null;
      Logger.debug(`Unsubscribe user pushes in dashboard`);
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

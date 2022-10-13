import { Component } from '@angular/core';
import {
  PageDto, SimplePageRequestDto, UserNotificationDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { UserNotificationsService } from "../services/user-notifications.service";
import { finalize } from "rxjs/internal/operators";

@Component({
  selector: 'app-notifications-center',
  templateUrl: './notifications-center.component.html',
  styleUrls: ['./notifications-center.component.scss']
})
export class NotificationsCenterComponent {

  private total: number = 0;
  private pageSize: number = 5;

  private virtualNotifications = new Map<number, UserNotificationDto>();

  private lastPageRequest: SimplePageRequestDto;

  //public virtualNotifications: UserNotificationDto[] = [];
  //const map = new Map<string, string>();
  public loading: boolean = false;

  public constructor(private userNotificationsService: UserNotificationsService,
                     private messageService: MessageService) {
  }

  public ngOnInit(): void {
  }

  public clear(): void {
    this.virtualNotifications.clear();
    this.total = 0;
    console.log('Clear notifications');
  }

  public hasMoreContent(): boolean {
    return this.virtualNotifications.size < this.total;
  }

  public isEmptyContent(): boolean {
    return this.total == 0;
  }

  public getNotifications(): UserNotificationDto[] {
    return Array.from(this.virtualNotifications.values());
  }

  public onLoad(): void {
    const rows = this.virtualNotifications.size;
    const page = Math.floor(rows / this.pageSize);
    console.log('Notification page ' + page);
    this.lastPageRequest = {
      page: page,
      size: this.pageSize
    };
    this.getNextPage(this.lastPageRequest, true);
  }

  private getNextPage(pageRequest: SimplePageRequestDto, readNotifications: boolean): void {
    this.loading = true;
    this.userNotificationsService.getNotifications(pageRequest)
      .subscribe({
        next: (pageDto: PageDto<UserNotificationDto>) => {
          this.total = pageDto.totalCount;
          pageDto.content.forEach((notification: UserNotificationDto) => {
            this.virtualNotifications.set(notification.id, notification);
          });
          if (readNotifications) {
            this.readNotificationsIfNeeded(pageDto.content)
          } else {
            this.loading = false;
          }
        },
        error: (error) => {
          this.loading = false;
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private readNotificationsIfNeeded(notifications: UserNotificationDto[]): void {
    const notReadIds: number[] = notifications
      .filter((notification: UserNotificationDto) => notification.messageStatus.value == 'NOT_READ')
      .map((notification: UserNotificationDto) => notification.id);
    if (notReadIds.length == 0) {
      console.log('All read content');
      this.loading = false;
    } else {
      const readNotificationsDto = {
        ids: notReadIds
      };
      console.log('Not read ids ' + notReadIds);
      this.userNotificationsService.readNotifications(readNotificationsDto)
        .subscribe({
          next: () => {
            this.getNextPage(this.lastPageRequest, false);
          },
          error: (error) => {
            this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
            this.loading = false;
          }
        });
    }
  }
}

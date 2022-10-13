import { Component } from '@angular/core';
import {
  PageDto, SimplePageRequestDto, UserNotificationDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { UserNotificationsService } from "../services/user-notifications.service";
import { Logger } from "../../common/util/logging";
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

  public loading: boolean = false;

  public constructor(private userNotificationsService: UserNotificationsService,
                     private messageService: MessageService) {
  }

  public ngOnInit(): void {
  }

  public clear(): void {
    this.virtualNotifications.clear();
    this.total = 0;
    Logger.debug(`Notifications has been cleared`);
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

  public readAllNotifications(): void {
    Logger.debug(`Starting to read all notifications`);
    this.userNotificationsService.readNotifications({ ids: [] })
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          Logger.debug(`All notifications has been read`);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public loadNext(): void {
    const rows = this.virtualNotifications.size;
    const page = Math.floor(rows / this.pageSize);
    this.lastPageRequest = {
      page: page,
      size: this.pageSize
    };
    this.getNextPage(this.lastPageRequest);
  }

  private getNextPage(pageRequest: SimplePageRequestDto): void {
    this.loading = true;
    Logger.debug(`Get next notifications page for page request ${this.lastPageRequest}`);
    this.userNotificationsService.getNotifications(pageRequest)
      .subscribe({
        next: (pageDto: PageDto<UserNotificationDto>) => {
          Logger.debug(`Receiver notifications page ${pageDto.page} with size ${pageDto.content.length} for page request ${this.lastPageRequest}`);
          this.total = pageDto.totalCount;
          pageDto.content.forEach((notification: UserNotificationDto) => {
            this.virtualNotifications.set(notification.id, notification);
          });
          this.readNotificationsIfNeeded(pageDto.content);
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
      Logger.debug(`All notification have already been read for page request ${this.lastPageRequest}`);
      this.loading = false;
    } else {
      const readNotificationsDto = {
        ids: notReadIds
      };
      Logger.debug(`Got not read notifications ids ${notReadIds} for page request ${this.lastPageRequest}`);
      this.userNotificationsService.readNotifications(readNotificationsDto)
        .subscribe({
          next: () => {
            this.getNextPage(this.lastPageRequest);
          },
          error: (error) => {
            this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
            this.loading = false;
          }
        });
    }
  }
}

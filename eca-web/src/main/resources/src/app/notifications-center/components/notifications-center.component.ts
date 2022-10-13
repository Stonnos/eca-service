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
  private pageSize = 5;

  public virtualNotifications: UserNotificationDto[] = [];
  public loading: boolean = false;

  public constructor(private userNotificationsService: UserNotificationsService,
                     private messageService: MessageService) {
  }

  public ngOnInit(): void {
  }

  public clear(): void {
    this.virtualNotifications = [];
    this.total = 0;
    console.log('Clear notifications');
  }

  public hasMoreContent(): boolean {
    return this.virtualNotifications.length < this.total;
  }

  public isEmptyContent(): boolean {
    return this.total == 0;
  }

  public onLoad(): void {
    const rows = this.virtualNotifications.length;
    const page = Math.floor(rows / this.pageSize);
    console.log('Notification page ' + page);
    const pageRequestDto = {
      page: page,
      size: this.pageSize
    };
    this.getNextPage(pageRequestDto);
  }

  private getNextPage(pageRequest: SimplePageRequestDto): void {
    this.loading = true;
    this.userNotificationsService.getNotifications(pageRequest)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (pageDto: PageDto<UserNotificationDto>) => {
          this.total = pageDto.totalCount;
          this.virtualNotifications.push(...pageDto.content);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }
}

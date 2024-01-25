import { Component, OnInit } from '@angular/core';
import {
 UserProfileNotificationEventOptionsDto, UserProfileNotificationOptionsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { finalize } from "rxjs/internal/operators";
import { UserProfileOptionsService } from "../../users/services/user-profile-options.service";
import { UpdateUserProfileNotificationOptionsModel } from "../../users/model/update-user-profile-notification-options.model";
import {
  UpdateUserNotificationEventOptionsRequest,
  UpdateUserProfileNotificationOptionsRequest
} from "../../users/model/update-user-profile-notification-options-request.model";
import { Logger } from "../../common/util/logging";

@Component({
  selector: 'app-user-profile-notification-options',
  templateUrl: './user-profile-notification-options.component.html',
  styleUrls: ['./user-profile-notification-options.component.scss']
})
export class UserProfileNotificationOptionsComponent implements OnInit {

  public userProfileNotificationOptionsDto: UserProfileNotificationOptionsDto;

  public updateUserProfileNotificationOptionsModel: UpdateUserProfileNotificationOptionsModel = new UpdateUserProfileNotificationOptionsModel();

  public notificationEventsTableCaption: string = 'События уведомлений';

  public loading: boolean = false;

  public constructor(private userProfileOptionsService: UserProfileOptionsService,
                     private messageService: MessageService) {
  }

  public ngOnInit() {
    this.getUserProfileNotificationOptions();
  }

  public changedPushEnabledSwitch(event): void {
    const updateUserProfileNotificationOptionsRequest = this.createUserProfileNotificationOptionsRequest();
    updateUserProfileNotificationOptionsRequest.webPushEnabled = event.checked;
    this.updateUserProfileNotificationOptions(updateUserProfileNotificationOptionsRequest);
  }

  public changedEmailEnabledSwitch(event): void {
    const updateUserProfileNotificationOptionsRequest = this.createUserProfileNotificationOptionsRequest();
    updateUserProfileNotificationOptionsRequest.emailEnabled = event.checked;
    this.updateUserProfileNotificationOptions(updateUserProfileNotificationOptionsRequest);
  }

  public changedNotificationEventSwitch(event, notificationType: string, eventType: string): void {
    const updateUserProfileNotificationOptionsRequest = this.createUserProfileNotificationOptionsRequest();
    const eventOptionsDto = this.userProfileNotificationOptionsDto.notificationEventOptions
      .filter((eventOptionsDto: UserProfileNotificationEventOptionsDto) => eventOptionsDto.eventType == eventType)
      .pop();
    const updateUserNotificationEventOptionsRequest = new UpdateUserNotificationEventOptionsRequest();
    updateUserNotificationEventOptionsRequest.eventType = eventType;
    updateUserNotificationEventOptionsRequest.emailEnabled = eventOptionsDto.emailEnabled;
    updateUserNotificationEventOptionsRequest.webPushEnabled = eventOptionsDto.webPushEnabled;
    switch (notificationType) {
      case 'EMAIL':
        updateUserNotificationEventOptionsRequest.emailEnabled = event.checked;
        break;
      case 'PUSH':
        updateUserNotificationEventOptionsRequest.webPushEnabled = event.checked;
        break;
      default:
        Logger.debug(`Can't handle ${notificationType} user notification type`);
    }
    updateUserProfileNotificationOptionsRequest.notificationEventOptions.push(updateUserNotificationEventOptionsRequest);
    this.updateUserProfileNotificationOptions(updateUserProfileNotificationOptionsRequest);
  }

  private createUserProfileNotificationOptionsRequest(): UpdateUserProfileNotificationOptionsRequest {
    const updateUserProfileNotificationOptionsRequest = new UpdateUserProfileNotificationOptionsRequest();
    updateUserProfileNotificationOptionsRequest.webPushEnabled = this.userProfileNotificationOptionsDto.webPushEnabled;
    updateUserProfileNotificationOptionsRequest.emailEnabled = this.userProfileNotificationOptionsDto.emailEnabled;
    return updateUserProfileNotificationOptionsRequest;
  }

  private getUserProfileNotificationOptions(): void {
    this.loading = true;
    this.userProfileOptionsService.getUserNotificationOptions()
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      ).subscribe({
        next: (profileNotificationOptionsDto: UserProfileNotificationOptionsDto) => {
          this.userProfileNotificationOptionsDto = profileNotificationOptionsDto;
          Logger.debug(`User profile notification options ${JSON.stringify(profileNotificationOptionsDto)} has been fetched`);
          this.updateUserProfileNotificationOptionsModel.emailEnabled = profileNotificationOptionsDto.emailEnabled;
          this.updateUserProfileNotificationOptionsModel.webPushEnabled = profileNotificationOptionsDto.webPushEnabled;
          this.updateUserProfileNotificationOptionsModel.notificationEventOptions =
            profileNotificationOptionsDto.notificationEventOptions
              .map((eventOptionsDto: UserProfileNotificationEventOptionsDto) => {
                return {
                  eventType: eventOptionsDto.eventType,
                  eventDescription: eventOptionsDto.eventDescription,
                  emailEnabled: eventOptionsDto.emailEnabled,
                  webPushEnabled: eventOptionsDto.webPushEnabled,
                  webPushSupported: eventOptionsDto.webPushSupported,
                  emailSupported: eventOptionsDto.emailSupported
                };
              });
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
    });
  }

  private updateUserProfileNotificationOptions(updateUserProfileNotificationOptionsRequest: UpdateUserProfileNotificationOptionsRequest): void {
    Logger.debug(`Starting to update user profile notification options: ${JSON.stringify(updateUserProfileNotificationOptionsRequest)}`);
    this.loading = true;
    this.userProfileOptionsService.updateUserProfileNotificationOptions(updateUserProfileNotificationOptionsRequest)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      ).subscribe({
      next: () => {
        Logger.debug(`User profile notification options has been updated: ${JSON.stringify(updateUserProfileNotificationOptionsRequest)}`);
        this.getUserProfileNotificationOptions();
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    });
  }
}

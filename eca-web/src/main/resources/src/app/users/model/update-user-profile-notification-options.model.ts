export class UpdateUserNotificationEventOptionsModel {
  eventType: string;

  eventDescription: string;

  emailEnabled: boolean;

  webPushEnabled: boolean;

  webPushSupported: boolean;

  emailSupported: boolean;
}

export class UpdateUserProfileNotificationOptionsModel {
  emailEnabled: boolean;

  webPushEnabled: boolean;

  notificationEventOptions: UpdateUserNotificationEventOptionsModel[] = [];

  public constructor() {
  }
}

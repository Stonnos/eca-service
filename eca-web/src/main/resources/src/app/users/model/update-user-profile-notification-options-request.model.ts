export class UpdateUserNotificationEventOptionsRequest {
  eventType: string;

  emailEnabled: boolean;

  webPushEnabled: boolean;
}

export class UpdateUserProfileNotificationOptionsRequest {
  emailEnabled: boolean;

  webPushEnabled: boolean;

  notificationEventOptions: UpdateUserNotificationEventOptionsRequest[] = [];

  public constructor() {
  }
}

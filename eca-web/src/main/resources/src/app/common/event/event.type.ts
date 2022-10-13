export enum EventType {
  TOKEN_REFRESHED = 'TOKEN_REFRESHED',
  TOKEN_EXPIRED = 'TOKEN_EXPIRED',
  LOGOUT = 'LOGOUT',
  INIT_PUSH = 'INIT_PUSH',
  CLOSE_PUSH = 'CLOSE_PUSH'
}

export class NotificationDto {
  messageType: string;
  messageText: string;
  createdAt: string;
}

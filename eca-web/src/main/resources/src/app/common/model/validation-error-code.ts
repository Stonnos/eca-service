export class ValidationErrorCode {
  public static readonly UNIQUE_LOGIN: string = 'UniqueLogin';
  public static readonly UNIQUE_EMAIL: string = 'UniqueEmail';
  public static readonly USER_EMAIL:  string = 'UserEmail';
  public static readonly UNIQUE_TABLE_NAME = 'UniqueTableName';
  public static readonly INVALID_PASSWORD: string = 'InvalidPassword';
  public static readonly ACTIVE_CHANGE_PASSWORD_REQUEST: string = 'ActiveChangePasswordRequest';
  public static readonly USER_LOCKED:  string = 'UserLocked';
  public static readonly INVALID_TOKEN: string = 'InvalidToken';
}

export class ValidationErrorCode {
  public static readonly UNIQUE_LOGIN: string = 'UniqueLogin';
  public static readonly UNIQUE_EMAIL: string = 'UniqueEmail';
  public static readonly USER_EMAIL:  string = 'UserEmail';
  public static readonly UNIQUE_TABLE_NAME = 'UniqueTableName';
  public static readonly INVALID_PASSWORD: string = 'InvalidPassword';
  public static readonly ACTIVE_CHANGE_PASSWORD_REQUEST: string = 'ActiveChangePasswordRequest';
  public static readonly ACTIVE_RESET_PASSWORD_REQUEST: string = 'ActiveResetPasswordRequest';
  public static readonly USER_LOCKED:  string = 'UserLocked';
  public static readonly INVALID_TOKEN: string = 'InvalidToken';
  public static readonly INVALID_TRAIN_DATA_FILE: string = 'InvalidFile';
  public static readonly PROCESS_FILE_ERROR: string = 'ProcessFileError';
  public static readonly ACTIVE_CHANGE_EMAIL_REQUEST: string = 'ActiveChangeEmailRequest';
  public static readonly EMAIL_ALREADY_BOUND: string = 'EmailAlreadyBound';
  public static readonly PASSWORDS_MATCHED: string = 'PasswordsMatched';
}

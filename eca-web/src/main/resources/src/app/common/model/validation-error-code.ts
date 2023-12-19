export class ValidationErrorCode {
  public static readonly UNIQUE_LOGIN: string = 'UniqueLogin';
  public static readonly UNIQUE_EMAIL: string = 'UniqueEmail';
  public static readonly USER_EMAIL:  string = 'UserEmail';
  public static readonly DUPLICATE_INSTANCES_NAME = 'DuplicateInstancesName';
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
  public static readonly EMPTY_DATA_SET: string = 'EmptyDataSet';
  public static readonly CLASS_VALUES_IS_TOO_LOW: string = 'ClassValuesIsTooLow';
  public static readonly INVALID_CLASS_ATTRIBUTE_TYPE: string = 'InvalidClassAttributeType';
  public static readonly CLASS_ATTRIBUTE_NOT_SELECTED: string = 'ClassAttributeNotSelected';
  public static readonly SELECTED_ATTRIBUTES_NUMBER_IS_TOO_LOW: string = 'SelectedAttributesNumberIsTooLow';
  public static readonly INSTANCES_NOT_FOUND: string = 'InstancesNotFound';
  public static readonly NOT_SAFE_PASSWORD: string = 'NotSafePassword';
  public static readonly CLASSIFIER_OPTIONS_NOT_FOUND: string = 'ClassifierOptionsNotFound';
  public static readonly INVALID_FORMAT: string = 'InvalidFormat';
  public static readonly INTERNAL_ERROR: string = 'InternalError';
}

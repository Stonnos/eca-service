import { Component, OnInit, ViewChild } from '@angular/core';
import {
  RoleDto, UserDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { UsersService } from "../../users/services/users.service";
import { MenuItem, MessageService } from "primeng/api";
import { UserFields } from "../../common/util/field-names";
import { Utils } from "../../common/util/utils";
import { FieldService } from "../../common/services/field.service";
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";
import { FileUpload } from "primeng/primeng";
import { finalize } from "rxjs/internal/operators";
import { ChangePasswordRequest } from "../../change-password/model/change-password.request";
import { UpdateUserInfoModel } from "../../users/model/update-user-info.model";

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  public user: UserDto;

  public tfaEnabled: boolean = false;

  public commonFields: any[] = [];

  public userMenuItems: MenuItem[];

  public changePasswordDialogVisibility: boolean = false;

  public changeEmailDialogVisibility: boolean = false;

  public changePasswordRequest: ChangePasswordRequest = new ChangePasswordRequest();

  public uploading: boolean = false;

  public loading: boolean = false;

  private readonly changePasswordRequestCreatedMessage: string =
    'На ваш email отправлено письмо с подтверждением смены пароля';

  public uploadPhotoErrorHeader: string = 'Не удалось загрузить фото';

  public invalidPersonNameErrorMessages: string[] = [
    'Поле должно содержать не менее 2-х символов и начинаться с заглавной буквы.',
    'Разрешены только буквы одного алфавита'
  ];

  public personNameRegex: string = Utils.PERSON_NAME_REGEX;
  public personNameMaxLength: number = Utils.PERSON_NAME_MAX_LENGTH;

  public confirmDialogVisibility: boolean = false;
  public confirmDialogMessage: string;

  //Max file size: 10MB
  public maxFileSize: number = 10000000;
  public invalidFileSizeMessageSummary: string = 'Недопустимый размер файла,';
  public invalidFileSizeMessageDetail: string = 'максимальный допустимый размер: 10 MB.';
  public invalidFileTypeMessageSummary: string = 'Некорректный тип файла,';
  public invalidFileTypeMessageDetail: string = 'допускаются только файлы графических форматов.';

  private photo: Blob;

  @ViewChild(FileUpload, { static: true })
  public fileUpload: FileUpload;

  public constructor(private usersService: UsersService,
                     private fieldService: FieldService,
                     private sanitizer: DomSanitizer,
                     private messageService: MessageService) {
    this.initCommonFields();
  }

  public ngOnInit() {
    this.getUser(true);
    this.initUserPhotoMenu();
  }

  public hasPhoto(): boolean {
    return this.photo != null;
  }

  public hasErrors(): boolean {
    return this.fileUpload.msgs && this.fileUpload.msgs.length > 0;
  }

  public onPhotoUpload(event: any): void {
    this.uploadPhoto(event.files[0]);
    this.fileUpload.clear();
  }

  public getBlobUrl(): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustResourceUrl(URL.createObjectURL(this.photo));
  }

  public getAbbreviatedUserName(): string {
    return this.user && this.user.login.substring(0, 1).toUpperCase();
  }

  public getValue(field: string) {
    if (this.user) {
      if (field == UserFields.ROLES) {
        return this.user.roles && this.user.roles.map((role: RoleDto) => role.description).join(',');
      } else {
        return this.fieldService.getFieldValue(field, this.user, Utils.MISSING_VALUE);
      }
    }
    return null;
  }

  public changedTfaSwitch(event): void {
    this.loading = true;
    this.usersService.setTfaEnabled(event.checked)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      ).subscribe({
        next: () => {
          this.getUser(false);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
    });
  }

  public hideUploadPhotoErrorModal(): void {
    this.fileUpload.msgs = [];
  }

  public showChangePasswordRequestDialog(): void {
    this.changePasswordDialogVisibility = true;
  }

  public onChangePasswordDialogVisibility(visible): void {
    this.changePasswordDialogVisibility = visible;
  }

  public onCreateChangePasswordRequest(): void {
    this.confirmDialogMessage = this.changePasswordRequestCreatedMessage;
    this.confirmDialogVisibility = true;
  }

  public hideConfirmDialog(): void {
    this.confirmDialogVisibility = false;
  }

  public showChangeEmailDialog(): void {
    this.changeEmailDialogVisibility = true;
  }

  public onChangeEmailDialogVisibility(visible): void {
    this.changeEmailDialogVisibility = visible;
  }

  public onChangeEmail(newEmail: string): void {
    this.confirmDialogMessage = `На электронный адрес ${newEmail} отправлено письмо со ссылкой для подтверждения`;
    this.confirmDialogVisibility = true;
  }

  public updateFirstName(value: string): void {
    const updateUserInfoModel: UpdateUserInfoModel = new UpdateUserInfoModel(value, this.user.lastName, this.user.middleName);
    this.updateUserInfo(updateUserInfoModel);
  }

  public updateLastName(value: string): void {
    const updateUserInfoModel: UpdateUserInfoModel = new UpdateUserInfoModel(this.user.firstName, value, this.user.middleName);
    this.updateUserInfo(updateUserInfoModel);
  }

  public updateMiddleName(value: string): void {
    const updateUserInfoModel: UpdateUserInfoModel = new UpdateUserInfoModel(this.user.firstName, this.user.lastName, value);
    this.updateUserInfo(updateUserInfoModel);
  }

  private updateUserInfo(updateUserInfo: UpdateUserInfoModel): void {
    this.loading = true;
    this.usersService.updateUserInfo(updateUserInfo)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.getUser(false);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private getUser(downloadPhoto: boolean): void {
    this.usersService.getCurrentUser().subscribe({
      next: (user: UserDto) => {
        this.user = user;
        this.tfaEnabled = user.tfaEnabled;
        if (downloadPhoto) {
          this.updatePhoto();
        }
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    });
  }

  private updatePhoto(): void {
    if (this.user.photoId) {
      this.downloadPhoto(this.user.photoId);
    } else {
      this.photo = null;
    }
  }

  private uploadPhoto(file: File): void {
    this.uploading = true;
    this.usersService.uploadPhoto(file)
      .pipe(
        finalize(() => {
          this.uploading = false;
        })
      )
      .subscribe({
        next: () => {
          this.getUser(true);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private downloadPhoto(id: number): void {
    this.uploading = true;
    this.usersService.downloadPhoto(id)
      .pipe(
        finalize(() => {
          this.uploading = false;
        })
      )
      .subscribe({
        next: (photo: Blob) => {
          this.photo = photo;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private deletePhoto(): void {
    this.uploading = true;
    this.usersService.deletePhoto()
      .pipe(
        finalize(() => {
          this.uploading = false;
        })
      )
      .subscribe({
        next: () => {
          this.getUser(true);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initCommonFields(): void {
    this.commonFields = [
      { name: UserFields.LAST_NAME, label: "Фамилия:" },
      { name: UserFields.FIRST_NAME, label: "Имя:" },
      { name: UserFields.MIDDLE_NAME, label: "Отчество:" },
      { name: UserFields.ROLES, label: "Роли:" },
      { name: UserFields.TFA_ENABLED, label: "Двухфакторная аутентификация:" },
      { name: UserFields.PASSWORD_DATE, label: "Дата изменения пароля:" },
    ];
  }

  private initUserPhotoMenu(): void {
    this.userMenuItems = [
      {
        label: 'Обновить',
        icon: 'pi pi-upload',
        command: () => {
          this.fileUpload.basicFileInput.nativeElement.click();
        }
      },
      {
        label: 'Удалить',
        icon: 'pi pi-fw pi-trash',
        command: () => {
          this.deletePhoto();
        }
      }
    ];
  }
}

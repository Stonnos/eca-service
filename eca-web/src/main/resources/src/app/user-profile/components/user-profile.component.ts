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

  public uploading = true;

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
    this.getUser();
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
    this.usersService.setTfaEnabled(event.checked).subscribe({
      next: () => {
        this.getUser();
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    })
  }

  public hide(): void {
    this.fileUpload.msgs = [];
  }

  private getUser(): void {
    this.usersService.getCurrentUser().subscribe({
      next: (user: UserDto) => {
        this.user = user;
        this.tfaEnabled = user.tfaEnabled;
        this.updatePhoto();
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    })
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
          this.getUser();
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
          this.getUser();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initCommonFields(): void {
    this.commonFields = [
      { name: UserFields.FIRST_NAME, label: "Имя:" },
      { name: UserFields.ROLES, label: "Роли:" },
      { name: UserFields.TFA_ENABLED, label: "Двухфакторная аутентификация:" }
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

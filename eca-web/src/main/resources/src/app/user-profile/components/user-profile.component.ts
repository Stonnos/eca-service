import { Component, OnInit } from '@angular/core';
import {
  RoleDto, UserDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { UsersService } from "../../users/services/users.service";
import { MenuItem, MessageService } from "primeng/api";
import { UserFields } from "../../common/util/field-names";
import { Utils } from "../../common/util/utils";
import { FieldService } from "../../common/services/field.service";
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";

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

  private file: File;

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
    return this.file != null;
  }

  public onPhotoUpload(event: any, fileUpload: any): void {
    this.file = event.files[0];
    fileUpload.clear();
  }

  public getBlobUrl(): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustResourceUrl(URL.createObjectURL(this.file));
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

  private getUser(): void {
    this.usersService.getCurrentUser().subscribe({
      next: (user: UserDto) => {
        this.user = user;
        this.tfaEnabled = user.tfaEnabled;
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
      }
    })
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
        label: 'Обновить фотографию',
        icon: 'pi pi-upload'
      },
      {
        label: 'Удалить фотографию',
        icon: 'pi pi-fw pi-trash',
        command: () => {
          this.file = null;
        }
      }
    ];
  }
}

import { Component, OnInit } from '@angular/core';
import {
  RoleDto, UserDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { UsersService } from "../../users/services/users.service";
import { MessageService } from "primeng/api";
import { UserFields } from "../../common/util/field-names";
import { Utils } from "../../common/util/utils";
import { FieldService } from "../../common/services/field.service";

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  public user: UserDto;

  public tfaEnabled: boolean = false;

  public commonFields: any[] = [];

  public constructor(private usersService: UsersService,
                     private fieldService: FieldService,
                     private messageService: MessageService) {
    this.initCommonFields();
  }

  public ngOnInit() {
    this.getUser();
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
}

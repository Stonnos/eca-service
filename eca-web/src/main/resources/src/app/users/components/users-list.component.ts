import { Component, Injector, OnInit } from '@angular/core';
import {
  PageDto,
  PageRequestDto, RoleDto, UserDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";
import { UserFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { UsersService } from "../services/users.service";
import { CreateUserModel } from "../../create-user/model/create-user.model";

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss']
})
export class UsersListComponent extends BaseListComponent<UserDto> implements OnInit {

  public createUserDialogVisibility: boolean = false;

  public createUserModel: CreateUserModel = new CreateUserModel();

  public constructor(private injector: Injector,
                     private usersService: UsersService) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = UserFields.CREATION_DATE;
    this.notSortableColumns = [UserFields.ROLES];
    this.initColumns();
  }

  public ngOnInit() {
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<UserDto>> {
    return this.usersService.getUsers(pageRequest);
  }

  public getColumnValue(column: string, item: UserDto) {
    if (column == UserFields.ROLES) {
      return item.roles && item.roles.map((role: RoleDto) => role.description).join(',');
    } else {
      return super.getColumnValue(column, item);
    }
  }

  public onCreateUserDialogVisibility(visible): void {
    this.createUserDialogVisibility = visible;
  }

  public onCreateUser(): void {
    this.refreshUsersPage();
  }

  public showCreateUserDialog(): void {
    this.createUserDialogVisibility = true;
  }

  private refreshUsersPage(): void {
    this.performPageRequest(0, this.pageSize, this.table.sortField, this.table.sortOrder == 1);
  }

  private initColumns() {
    this.columns = [
      { name: UserFields.LOGIN, label: "Логин" },
      { name: UserFields.EMAIL, label: "Email пользователя" },
      { name: UserFields.FIRST_NAME, label: "Имя пользователя" },
      { name: UserFields.CREATION_DATE, label: "Дата создания" },
      { name: UserFields.ROLES, label: "Роли" }
    ];
  }
}

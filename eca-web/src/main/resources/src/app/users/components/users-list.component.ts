import { Component, Injector, OnInit } from '@angular/core';
import {
  PageDto,
  PageRequestDto, RoleDto, UserDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService, ConfirmationService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";
import { UserFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { UsersService } from "../services/users.service";
import { CreateUserModel } from "../../create-user/model/create-user.model";
import { finalize } from "rxjs/internal/operators";
import { UserFilterFields } from "../../common/util/filter-field-names";

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss']
})
export class UsersListComponent extends BaseListComponent<UserDto> implements OnInit {

  public createUserDialogVisibility: boolean = false;

  public createUserModel: CreateUserModel = new CreateUserModel();

  public constructor(private injector: Injector,
                     private usersService: UsersService,
                     private confirmationService: ConfirmationService) {
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

  public onCreateUser(user: UserDto): void {
    this.messageService.add({ severity: 'success', summary: `Создан новый пользователь ${user.login}`, detail: '' });
    this.lastCreatedId = user.id;
    this.reloadPageWithLoader();
  }

  public showCreateUserDialog(): void {
    this.createUserDialogVisibility = true;
  }

  public lockUser(user: UserDto): void {
    this.confirmationService.confirm({
      header: 'Блокировка пользователя',
      message: `Вы уверены, что хотите заблокировать пользователя ${user.login}?`,
      acceptLabel: 'Да',
      rejectLabel: 'Нет',
      accept: () => {
        this.lockUserById(user.id);
      }
    });
  }

  public unlockUser(user: UserDto): void {
    this.confirmationService.confirm({
      header: 'Снятие блокировки пользователя',
      message: `Вы уверены, что хотите снять блокировку пользователя ${user.login}?`,
      acceptLabel: 'Да',
      rejectLabel: 'Нет',
      accept: () => {
        this.unlockUserById(user.id);
      }
    });
  }

  private lockUserById(userId: number): void {
    this.loading = true;
    this.usersService.lock(userId)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.reloadPageWithLoader();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private unlockUserById(userId: number): void {
    this.loading = true;
    this.usersService.unlock(userId)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.reloadPageWithLoader();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initColumns() {
    this.columns = [
      { name: UserFields.LOGIN, label: "Логин", sortBy: UserFilterFields.LOGIN },
      { name: UserFields.EMAIL, label: "Email пользователя", sortBy: UserFilterFields.EMAIL },
      { name: UserFields.FULL_NAME, label: "ФИО пользователя", sortBy: UserFilterFields.FULL_NAME },
      { name: UserFields.CREATION_DATE, label: "Дата создания", sortBy: UserFilterFields.CREATION_DATE },
      { name: UserFields.ROLES, label: "Роли" }
    ];
  }
}

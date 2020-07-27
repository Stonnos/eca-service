import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifiersConfigurationDto,
  InstancesDto,
  PageDto,
  PageRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ConfirmationService, MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";
import { InstancesFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { CreateUserModel } from "../../create-user/model/create-user.model";
import { InstancesService } from "../services/instances.service";
import { finalize } from "rxjs/internal/operators";

@Component({
  selector: 'app-instances-list',
  templateUrl: './instances-list.component.html',
  styleUrls: ['./instances-list.component.scss']
})
export class InstancesListComponent extends BaseListComponent<InstancesDto> implements OnInit {

  public createUserDialogVisibility: boolean = false;

  public createUserModel: CreateUserModel = new CreateUserModel();

  public constructor(private injector: Injector,
                     private confirmationService: ConfirmationService,
                     private instancesService: InstancesService) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = InstancesFields.CREATED;
    this.initColumns();
  }

  public ngOnInit() {
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<InstancesDto>> {
    return this.instancesService.getInstancesPage(pageRequest);
  }

  /*public onCreateUserDialogVisibility(visible): void {
    this.createUserDialogVisibility = visible;
  }

  public onCreateUser(user: UserDto): void {
    this.messageService.add({ severity: 'success', summary: `Создан новый пользователь ${user.login}`, detail: '' });
    this.lastCreatedId = user.id;
    this.refreshUsersPage();
  }

  public showCreateUserDialog(): void {
    this.createUserDialogVisibility = true;
  }*/

  private refreshInstancesPage(): void {
    this.performPageRequest(0, this.pageSize, this.table.sortField, this.table.sortOrder == 1);
  }

  public onDeleteInstances(item: InstancesDto): void {
    this.confirmationService.confirm({
      message: 'Вы уверены?',
      acceptLabel: 'Да',
      rejectLabel: 'Нет',
      accept: () => {
        this.deleteInstances(item);
      }
    });
  }

  private deleteInstances(item: InstancesDto): void {
    this.loading = true;
    this.instancesService.deleteInstances(item.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: `Данные ${item.tableName} были успешно удалены`, detail: '' });
          this.refreshInstancesPage();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initColumns() {
    this.columns = [
      { name: InstancesFields.TABLE_NAME, label: "Название таблицы" },
      { name: InstancesFields.NUM_INSTANCES, label: "Число объектов" },
      { name: InstancesFields.NUM_ATTRIBUTES, label: "Число атрибутов" },
      { name: InstancesFields.CREATED, label: "Дата создания" },
      { name: InstancesFields.CREATED_BY, label: "Пользователь" },
    ];
  }
}

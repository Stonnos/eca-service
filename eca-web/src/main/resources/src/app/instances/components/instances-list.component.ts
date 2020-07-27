import { Component, Injector, OnInit } from '@angular/core';
import {
  InstancesDto,
  PageDto,
  PageRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";
import { InstancesFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { CreateUserModel } from "../../create-user/model/create-user.model";
import { InstancesService } from "../services/instances.service";

@Component({
  selector: 'app-instances-list',
  templateUrl: './instances-list.component.html',
  styleUrls: ['./instances-list.component.scss']
})
export class InstancesListComponent extends BaseListComponent<InstancesDto> implements OnInit {

  public createUserDialogVisibility: boolean = false;

  public createUserModel: CreateUserModel = new CreateUserModel();

  public constructor(private injector: Injector, private instancesService: InstancesService) {
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
  }

  private refreshUsersPage(): void {
    this.performPageRequest(0, this.pageSize, this.table.sortField, this.table.sortOrder == 1);
  }*/

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

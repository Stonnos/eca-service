import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifiersConfigurationDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MenuItem, MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";
import { ClassifiersConfigurationFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { ClassifiersConfigurationsService } from "../services/classifiers-configurations.service";
import { finalize } from "rxjs/internal/operators";
import { ClassifiersConfigurationModel } from "../../create-classifiers-configuration/model/classifiers-configuration.model";

@Component({
  selector: 'app-classifiers-configurations',
  templateUrl: './classifiers-configurations.component.html',
  styleUrls: ['./classifiers-configurations.component.scss']
})
export class ClassifiersConfigurationsComponent extends BaseListComponent<ClassifiersConfigurationDto> implements OnInit {

  public setActiveMenuItem: MenuItem;
  public deleteMenuItem: MenuItem;
  public renameMenuItem: MenuItem;
  public uploadClassifiersOptionsMenu: MenuItem;
  public optionsMenu: MenuItem[] = [];
  public selectedConfiguration: ClassifiersConfigurationDto;

  public classifiersConfiguration: ClassifiersConfigurationModel = new ClassifiersConfigurationModel();
  public editClassifiersConfigurationDialogVisibility: boolean = false;
  public uploadClassifiersOptionsDialogVisibility: boolean = false;

  public lastCreatedConfigurationId: number;
  public blinkConfigurationId: number;

  public constructor(private injector: Injector,
                     private classifierOptionsService: ClassifiersConfigurationsService) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = ClassifiersConfigurationFields.CREATED;
    this.notSortableColumns = [ClassifiersConfigurationFields.CLASSIFIERS_OPTIONS_COUNT];
    this.linkColumns = [ClassifiersConfigurationFields.CONFIGURATION_NAME];
    this.initColumns();
  }

  public ngOnInit() {
    this.initMenu();
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ClassifiersConfigurationDto>> {
    return this.classifierOptionsService.getClassifiersConfigurations(pageRequest);
  }

  public setPage(pageDto: PageDto<ClassifiersConfigurationDto>) {
    this.blinkConfigurationId = this.lastCreatedConfigurationId;
    this.lastCreatedConfigurationId = null;
    super.setPage(pageDto);
  }

  public showUploadClassifiersOptionsDialogVisibility(): void {
    this.uploadClassifiersOptionsDialogVisibility = true;
  }

  public onUploadClassifiersOptionsDialogVisibility(visible): void {
    this.uploadClassifiersOptionsDialogVisibility = visible;
  }

  public onMouseClickMenu(event: any, item: ClassifiersConfigurationDto): void {
    this.selectedConfiguration = item;
    this.setActiveMenuItem.visible = !item.active;
    this.deleteMenuItem.visible = !item.buildIn && !item.active;
  }

  public showEditClassifiersConfigurationDialog(item?: ClassifiersConfigurationDto): void {
    if (item && item.id) {
      this.classifiersConfiguration = { id: item.id, configurationName: item.configurationName };
    } else {
      this.classifiersConfiguration = new ClassifiersConfigurationModel();
    }
    this.editClassifiersConfigurationDialogVisibility = true;
  }

  public onEditClassifiersConfigurationDialogVisibility(visible): void {
    this.editClassifiersConfigurationDialogVisibility = visible;
  }

  public onEditClassifiersConfiguration(item: ClassifiersConfigurationModel): void {
    if (item.id) {
      this.updateConfiguration(item);
    } else {
      this.createConfiguration(item);
    }
  }

  public isBlink(item: ClassifiersConfigurationDto): boolean {
    return this.blinkConfigurationId == item.id;
  }

  private initColumns() {
    this.columns = [
      { name: ClassifiersConfigurationFields.CONFIGURATION_NAME, label: "Конфигурация" },
      { name: ClassifiersConfigurationFields.CREATED, label: "Дата создания" },
      { name: ClassifiersConfigurationFields.UPDATED, label: "Дата обновления" },
      { name: ClassifiersConfigurationFields.CLASSIFIERS_OPTIONS_COUNT, label: "Число настроек классификаторов" },
    ];
  }

  private refreshClassifiersConfigurationsPage(): void {
    this.performPageRequest(0, this.pageSize, ClassifiersConfigurationFields.CREATED, false);
  }

  private createConfiguration(item: ClassifiersConfigurationModel): void {
    this.loading = true;
    this.classifierOptionsService.saveConfiguration({ configurationName: item.configurationName })
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (configuration: ClassifiersConfigurationDto) => {
          this.lastCreatedConfigurationId = configuration.id;
          this.messageService.add({ severity: 'success', summary: `Добавлена конфигурация ${configuration.configurationName}`, detail: '' });
          this.refreshClassifiersConfigurationsPage();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private updateConfiguration(item: ClassifiersConfigurationModel): void {
    this.loading = true;
    this.classifierOptionsService.updateConfiguration({ id: item.id, configurationName: item.configurationName })
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.refreshClassifiersConfigurationsPage();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private deleteConfiguration(): void {
    this.loading = true;
    this.classifierOptionsService.deleteConfiguration(this.selectedConfiguration.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: `Конфигурация ${this.selectedConfiguration.configurationName} была удалена`, detail: '' });
          this.refreshClassifiersConfigurationsPage();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private setActiveConfiguration(): void {
    this.loading = true;
    this.classifierOptionsService.setActive(this.selectedConfiguration.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.refreshClassifiersConfigurationsPage();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private initMenu() {
    this.uploadClassifiersOptionsMenu = {
      label: 'Загрузить настройки',
      icon: 'pi pi-upload',
      command: () => {
        this.showUploadClassifiersOptionsDialogVisibility();
      }
    };
    this.setActiveMenuItem = {
      label: 'Сделать активной',
      icon: 'pi pi-tag',
      command: () => {
        this.setActiveConfiguration();
      }
    };
    this.deleteMenuItem = {
      label: 'Удалить',
      icon: 'pi pi-fw pi-trash',
      command: () => {
        this.deleteConfiguration();
      }
    };
    this.renameMenuItem = {
      label: 'Переименовать',
      icon: 'pi pi-fw pi-pencil',
      command: () => {
        this.showEditClassifiersConfigurationDialog(this.selectedConfiguration);
      }
    };
    this.optionsMenu = [
      {
        icon: 'pi pi-fw pi-cog',
        styleClass: 'main-menu-item',
        items: [this.renameMenuItem, this.deleteMenuItem, this.setActiveMenuItem, this.uploadClassifiersOptionsMenu]
      }
    ];
  }
}

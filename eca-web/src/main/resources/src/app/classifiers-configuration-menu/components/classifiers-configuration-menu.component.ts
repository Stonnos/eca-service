import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import {
  ClassifiersConfigurationDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MenuItem } from "primeng/api";

@Component({
  selector: 'app-classifiers-configuration-menu',
  templateUrl: './classifiers-configuration-menu.component.html',
  styleUrls: ['./classifiers-configuration-menu.component.scss']
})
export class ClassifiersConfigurationMenuComponent implements OnInit, OnChanges {

  public setActiveMenuItem: MenuItem;
  public deleteMenuItem: MenuItem;
  public renameMenuItem: MenuItem;
  public uploadClassifiersOptionsMenu: MenuItem;
  public optionsMenu: MenuItem[] = [];

  @Input()
  public classifiersConfiguration: ClassifiersConfigurationDto;

  @Output()
  public onUploadClassifiers: EventEmitter<ClassifiersConfigurationDto> = new EventEmitter<ClassifiersConfigurationDto>();
  @Output()
  public onRename: EventEmitter<ClassifiersConfigurationDto> = new EventEmitter<ClassifiersConfigurationDto>();
  @Output()
  public onDelete: EventEmitter<ClassifiersConfigurationDto> = new EventEmitter<ClassifiersConfigurationDto>();
  @Output()
  public onSetActive: EventEmitter<ClassifiersConfigurationDto> = new EventEmitter<ClassifiersConfigurationDto>();

  public ngOnInit() {
  }

  public ngOnChanges(changes: SimpleChanges): void {
    this.initMenu();
  }

  private initMenu() {
    if (this.classifiersConfiguration) {
      this.uploadClassifiersOptionsMenu = {
        label: ' Загрузить классификаторы',
        icon: 'pi pi-upload',
        styleClass: 'menu-item',
        visible: !this.classifiersConfiguration.buildIn,
        command: () => {
          this.onUploadClassifiers.emit(this.classifiersConfiguration);
        }
      };
      this.setActiveMenuItem = {
        label: 'Сделать активной',
        icon: 'pi pi-tag',
        styleClass: 'menu-item',
        visible: !this.classifiersConfiguration.active && this.classifiersConfiguration.classifiersOptionsCount > 0,
        command: () => {
          this.onSetActive.emit(this.classifiersConfiguration);
        }
      };
      this.deleteMenuItem = {
        label: 'Удалить',
        icon: 'pi pi-fw pi-trash',
        styleClass: 'menu-item',
        visible: !this.classifiersConfiguration.buildIn && !this.classifiersConfiguration.active,
        command: () => {
          this.onDelete.emit(this.classifiersConfiguration);
        }
      };
      this.renameMenuItem = {
        label: 'Переименовать',
        icon: 'pi pi-fw pi-pencil',
        styleClass: 'menu-item',
        command: () => {
          this.onRename.emit(this.classifiersConfiguration);
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
}

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  ClassifiersConfigurationDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { MenuItem } from "primeng/api";

@Component({
  selector: 'app-classifiers-configuration-menu',
  templateUrl: './classifiers-configuration-menu.component.html',
  styleUrls: ['./classifiers-configuration-menu.component.scss']
})
export class ClassifiersConfigurationMenuComponent implements OnInit {

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
    this.initMenu();
  }

  private initMenu() {
    this.uploadClassifiersOptionsMenu = {
      label: 'Классификаторы',
      icon: 'pi pi-upload',
      visible: !this.classifiersConfiguration.buildIn,
      command: () => {
        this.onUploadClassifiers.emit(this.classifiersConfiguration);
      }
    };
    this.setActiveMenuItem = {
      label: 'Сделать активной',
      icon: 'pi pi-tag',
      visible: !this.classifiersConfiguration.active && this.classifiersConfiguration.classifiersOptionsCount > 0,
      command: () => {
        this.onSetActive.emit(this.classifiersConfiguration);
      }
    };
    this.deleteMenuItem = {
      label: 'Удалить',
      icon: 'pi pi-fw pi-trash',
      visible: !this.classifiersConfiguration.buildIn && !this.classifiersConfiguration.active,
      command: () => {
        this.onDelete.emit(this.classifiersConfiguration);
      }
    };
    this.renameMenuItem = {
      label: 'Переименовать',
      icon: 'pi pi-fw pi-pencil',
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

import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import {
  ClassifiersConfigurationDto, FormTemplateDto
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
  public copyMenuItem: MenuItem;
  public renameMenuItem: MenuItem;
  public uploadClassifiersOptionsMenu: MenuItem;
  public addClassifiersOptionsMenu: MenuItem;
  public downloadReportMenu: MenuItem;
  public optionsMenu: MenuItem[] = [];

  @Input()
  public classifiersConfiguration: ClassifiersConfigurationDto;
  @Input()
  public templates: FormTemplateDto[] = [];

  @Output()
  public onUploadClassifiers: EventEmitter<ClassifiersConfigurationDto> = new EventEmitter<ClassifiersConfigurationDto>();
  @Output()
  public onRename: EventEmitter<ClassifiersConfigurationDto> = new EventEmitter<ClassifiersConfigurationDto>();
  @Output()
  public onCopy: EventEmitter<ClassifiersConfigurationDto> = new EventEmitter<ClassifiersConfigurationDto>();
  @Output()
  public onDelete: EventEmitter<ClassifiersConfigurationDto> = new EventEmitter<ClassifiersConfigurationDto>();
  @Output()
  public onSetActive: EventEmitter<ClassifiersConfigurationDto> = new EventEmitter<ClassifiersConfigurationDto>();
  @Output()
  public onDownloadReport: EventEmitter<ClassifiersConfigurationDto> = new EventEmitter<ClassifiersConfigurationDto>();

  public ngOnInit() {
  }

  public ngOnChanges(changes: SimpleChanges): void {
    this.initMenu();
  }

  private initMenu() {
    if (this.classifiersConfiguration) {
      this.uploadClassifiersOptionsMenu = {
        label: 'Загрузить классификаторы из файла',
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
      this.copyMenuItem = {
        label: 'Копировать',
        icon: 'pi pi-fw pi-copy',
        styleClass: 'menu-item',
        command: () => {
          this.onCopy.emit(this.classifiersConfiguration);
        }
      };
      this.downloadReportMenu = {
        label: 'Сформировать отчет',
        icon: 'pi pi-file',
        styleClass: 'menu-item',
        command: () => {
          this.onDownloadReport.emit(this.classifiersConfiguration);
        }
      };
      const items: MenuItem[] = [
        this.renameMenuItem,
        this.deleteMenuItem,
        this.copyMenuItem,
        this.setActiveMenuItem,
        this.uploadClassifiersOptionsMenu,
        this.downloadReportMenu
      ];
      this.appendClassifierOptionsAddMenu(items);
      this.optionsMenu = [
        {
          icon: 'pi pi-fw pi-cog',
          styleClass: 'main-menu-item',
          items: items
        }
      ];
    }
  }

  private appendClassifierOptionsAddMenu(items: MenuItem[]): void {
    if (this.templates && this.templates.length > 0) {
      const classifiersItems: MenuItem[] = this.templates.map((template: FormTemplateDto) => {
        return {
          label: template.templateTitle,
          command: () => {
            console.log(template);
          }
        };
      });
      this.addClassifiersOptionsMenu = {
        label: 'Добавить классификатор',
        icon: 'pi pi-plus',
        styleClass: 'menu-item',
        visible: !this.classifiersConfiguration.buildIn,
        items: classifiersItems
      };
      items.push(this.addClassifiersOptionsMenu);
    }
  }
}

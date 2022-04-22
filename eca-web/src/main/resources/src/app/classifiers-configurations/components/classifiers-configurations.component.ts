import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifiersConfigurationDto, FormFieldDto, FormTemplateDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ConfirmationService, MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { Observable } from "rxjs/internal/Observable";
import { ClassifiersConfigurationFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { ClassifiersConfigurationsService } from "../services/classifiers-configurations.service";
import { finalize } from "rxjs/internal/operators";
import { ClassifiersConfigurationModel } from "../../create-classifiers-configuration/model/classifiers-configuration.model";
import { Router } from "@angular/router";
import { RouterPaths } from "../../routing/router-paths";
import { Utils } from "../../common/util/utils";
import { OperationType } from "../../common/model/operation-type.enum";
import { FormTemplatesService } from "../../form-templates/services/form-templates.service";
import { FormTemplatesMapper } from "../../form-templates/services/form-templates.mapper";
import { FormField } from "../../form-templates/model/form-template.model";

@Component({
  selector: 'app-classifiers-configurations',
  templateUrl: './classifiers-configurations.component.html',
  styleUrls: ['./classifiers-configurations.component.scss']
})
export class ClassifiersConfigurationsComponent extends BaseListComponent<ClassifiersConfigurationDto> implements OnInit {

  public selectedConfiguration: ClassifiersConfigurationDto;

  public classifiersConfiguration: ClassifiersConfigurationModel = new ClassifiersConfigurationModel();
  public editClassifiersConfigurationDialogVisibility: boolean = false;
  public uploadClassifiersOptionsDialogVisibility: boolean = false;
  public addClassifiersOptionsDialogVisibility: boolean = false;

  public templates: FormTemplateDto[] = [];

  public selectedTemplate: FormTemplateDto;
  public selectedFormFields: FormField[] = [];

  public constructor(private injector: Injector,
                     private classifiersConfigurationsService: ClassifiersConfigurationsService,
                     private formTemplatesService: FormTemplatesService,
                     private formTemplatesMapper: FormTemplatesMapper,
                     private confirmationService: ConfirmationService,
                     private router: Router) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = ClassifiersConfigurationFields.CREATION_DATE;
    this.notSortableColumns = [ClassifiersConfigurationFields.CLASSIFIERS_OPTIONS_COUNT];
    this.linkColumns = [ClassifiersConfigurationFields.ID];
    this.initColumns();
  }

  public ngOnInit() {
    this.getClassifiersTemplates();
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ClassifiersConfigurationDto>> {
    return this.classifiersConfigurationsService.getClassifiersConfigurations(pageRequest);
  }

  public onLink(event: any, column: string, item: ClassifiersConfigurationDto): void {
    if (column === ClassifiersConfigurationFields.ID) {
      this.router.navigate([RouterPaths.CLASSIFIERS_CONFIGURATION_DETAILS_URL, item.id]);
    } else {
      this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${column} as link`});
    }
  }

  public showUploadClassifiersOptionsDialogVisibility(item?: ClassifiersConfigurationDto): void {
    this.selectedConfiguration = item;
    this.uploadClassifiersOptionsDialogVisibility = true;
  }

  public onUploadClassifiersOptionsDialogVisibility(visible): void {
    this.uploadClassifiersOptionsDialogVisibility = visible;
  }

  public showCopyClassifiersConfigurationDialog(item?: ClassifiersConfigurationDto): void {
    this.classifiersConfiguration = new ClassifiersConfigurationModel(OperationType.COPY, item.id, item.configurationName);
    this.editClassifiersConfigurationDialogVisibility = true;
  }

  public showEditClassifiersConfigurationDialog(item?: ClassifiersConfigurationDto): void {
    if (item && item.id) {
      this.classifiersConfiguration = new ClassifiersConfigurationModel(OperationType.EDIT, item.id, item.configurationName);
    } else {
      this.classifiersConfiguration = new ClassifiersConfigurationModel(OperationType.CREATE);
    }
    this.editClassifiersConfigurationDialogVisibility = true;
  }

  public onEditClassifiersConfigurationDialogVisibility(visible): void {
    this.editClassifiersConfigurationDialogVisibility = visible;
  }

  public onAddClassifierOptionsDialogVisibility(visible): void {
    this.addClassifiersOptionsDialogVisibility = visible;
  }

  public onAddClassifierOptions(formFields: FormFieldDto[]): void {
    console.log('On add: ' + formFields);
  }

  public onEditClassifiersConfiguration(item: ClassifiersConfigurationModel): void {
    switch (item.operation) {
      case OperationType.CREATE:
        this.createConfiguration(item);
        break;
      case OperationType.EDIT:
        this.updateConfiguration(item);
        break;
      case OperationType.COPY:
        this.copyConfiguration(item);
        break;
      default:
        this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${item.operation} operation`});
    }
  }

  public onDeleteClassifiersConfiguration(item: ClassifiersConfigurationDto): void {
    this.confirmationService.confirm({
      message: 'Вы уверены?',
      acceptLabel: 'Да',
      rejectLabel: 'Нет',
      accept: () => {
        this.deleteConfiguration(item);
      }
    });
  }

  public onSetActiveClassifiersConfiguration(item: ClassifiersConfigurationDto): void {
    this.setActiveConfiguration(item);
  }

  public onDownloadReport(item: ClassifiersConfigurationDto): void {
    const observable = this.classifiersConfigurationsService.getClassifiersConfigurationReport(item.id);
    this.downloadReport(observable, Utils.getClassifiersConfigurationFile(item));
  }

  public onChooseClassifierOptionsTemplate(template: FormTemplateDto): void {
    this.selectedTemplate = template;
    this.selectedFormFields = this.formTemplatesMapper.mapToFormFields(template.fields);
    this.addClassifiersOptionsDialogVisibility = true;
  }

  public onUploadedClassifiersOptions(event): void {
    this.reloadPageWithLoader();
  }

  private initColumns() {
    this.columns = [
      { name: ClassifiersConfigurationFields.ID, label: "#" },
      { name: ClassifiersConfigurationFields.CONFIGURATION_NAME, label: "Конфигурация" },
      { name: ClassifiersConfigurationFields.CREATION_DATE, label: "Дата создания" },
      { name: ClassifiersConfigurationFields.UPDATED, label: "Дата обновления" },
      { name: ClassifiersConfigurationFields.CREATED_BY, label: "Пользователь" },
      { name: ClassifiersConfigurationFields.CLASSIFIERS_OPTIONS_COUNT, label: "Число настроек классификаторов" },
    ];
  }

  private createConfiguration(item: ClassifiersConfigurationModel): void {
    this.loading = true;
    this.classifiersConfigurationsService.saveConfiguration({ configurationName: item.configurationName })
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (configuration: ClassifiersConfigurationDto) => {
          this.lastCreatedId = configuration.id;
          this.messageService.add({ severity: 'success', summary: `Добавлена конфигурация ${configuration.configurationName}`, detail: '' });
          this.reloadPageWithLoader();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private updateConfiguration(item: ClassifiersConfigurationModel): void {
    this.loading = true;
    this.classifiersConfigurationsService.updateConfiguration({ id: item.id, configurationName: item.configurationName })
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

  private copyConfiguration(item: ClassifiersConfigurationModel): void {
    this.loading = true;
    this.classifiersConfigurationsService.copyConfiguration({ id: item.id, configurationName: item.configurationName })
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (configuration: ClassifiersConfigurationDto) => {
          this.lastCreatedId = configuration.id;
          this.messageService.add({ severity: 'success',
            summary: `Создана копия конфигурации с именем ${configuration.configurationName}`, detail: '' });
          this.reloadPageWithLoader();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private deleteConfiguration(item: ClassifiersConfigurationDto): void {
    this.loading = true;
    this.classifiersConfigurationsService.deleteConfiguration(item.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: `Конфигурация ${item.configurationName} была удалена`, detail: '' });
          this.reloadPageWithLoader();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private setActiveConfiguration(item: ClassifiersConfigurationDto): void {
    this.loading = true;
    this.classifiersConfigurationsService.setActive(item.id)
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

  private getClassifiersTemplates(): void {
    this.formTemplatesService.getClassifiersFormTemplates()
      .subscribe({
        next: (templates: FormTemplateDto[]) => {
          this.templates = templates;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }
}

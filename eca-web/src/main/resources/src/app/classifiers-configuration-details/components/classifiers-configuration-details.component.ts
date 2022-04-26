import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifierOptionsDto, ClassifiersConfigurationDto, FormTemplateDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ClassifierOptionsService } from "../services/classifier-options.service";
import { ConfirmationService, MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { OverlayPanel} from "primeng/primeng";
import { Observable } from "rxjs/internal/Observable";
import { ClassifierOptionsFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { ActivatedRoute, Router } from "@angular/router";
import { ClassifiersConfigurationsService } from "../../classifiers-configurations/services/classifiers-configurations.service";
import { ClassifiersConfigurationModel } from "../../create-classifiers-configuration/model/classifiers-configuration.model";
import { ExperimentTabUtils } from "../../experiments-tabs/model/experiment-tab.utils";
import { finalize } from "rxjs/internal/operators";
import { Utils } from "../../common/util/utils";
import { OperationType }  from "../../common/model/operation-type.enum";
import { FormTemplatesService } from "../../form-templates/services/form-templates.service";
import { FormField } from "../../form-templates/model/form-template.model";
import { FormTemplatesMapper } from "../../form-templates/services/form-templates.mapper";

@Component({
  selector: 'app-classifiers-configuration-details',
  templateUrl: './classifiers-configuration-details.component.html',
  styleUrls: ['./classifiers-configuration-details.component.scss']
})
export class ClassifiersConfigurationDetailsComponent extends BaseListComponent<ClassifierOptionsDto> implements OnInit {

  private readonly configurationId: number;

  public classifiersConfiguration: ClassifiersConfigurationDto;

  public editClassifiersConfiguration: ClassifiersConfigurationModel = new ClassifiersConfigurationModel();

  public selectedOptions: ClassifierOptionsDto;

  public editClassifiersConfigurationDialogVisibility: boolean = false;
  public uploadClassifiersOptionsDialogVisibility: boolean = false;
  public addClassifiersOptionsDialogVisibility: boolean = false;

  public templates: FormTemplateDto[] = [];

  public selectedTemplate: FormTemplateDto;
  public selectedFormFields: FormField[] = [];

  public constructor(private injector: Injector,
                     private classifierOptionsService: ClassifierOptionsService,
                     private classifiersConfigurationService: ClassifiersConfigurationsService,
                     private formTemplatesService: FormTemplatesService,
                     private formTemplatesMapper: FormTemplatesMapper,
                     private route: ActivatedRoute,
                     private confirmationService: ConfirmationService,
                     private router: Router) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.configurationId = this.route.snapshot.params.id;
    this.defaultSortField = ClassifierOptionsFields.CREATION_DATE;
    this.linkColumns = [ClassifierOptionsFields.OPTIONS_DESCRIPTION];
    this.initColumns();
  }

  public ngOnInit() {
    this.getClassifiersConfigurationDetails();
    this.getClassifiersTemplates();
  }

  public getClassifiersConfigurationDetails(): void {
    this.classifiersConfigurationService.getClassifiersConfigurationDetails(this.configurationId)
      .subscribe({
        next: (classifiersConfiguration: ClassifiersConfigurationDto) => {
          this.classifiersConfiguration = classifiersConfiguration;
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsDto>> {
    return this.classifierOptionsService.getClassifiersOptions(this.configurationId, pageRequest);
  }

  public onSelect(event, classifierOptionsDto: ClassifierOptionsDto, overlayPanel: OverlayPanel) {
    this.selectedOptions = classifierOptionsDto;
    overlayPanel.toggle(event);
  }

  public isDeleteAllowed(): boolean {
    return this.classifiersConfiguration && !this.classifiersConfiguration.buildIn && this.hasMoreThanOneOptionsForActiveConfiguration();
  }

  public onDeleteClassifierOptions(event: any, item: ClassifierOptionsDto): void {
    this.deleteClassifierOptions(item);
  }

  public onUploadClassifiersOptionsDialogVisibility(visible): void {
    this.uploadClassifiersOptionsDialogVisibility = visible;
  }

  public showUploadClassifiersOptionsDialogVisibility(item: ClassifiersConfigurationDto): void {
    this.uploadClassifiersOptionsDialogVisibility = true;
  }

  public onEditClassifiersConfigurationDialogVisibility(visible): void {
    this.editClassifiersConfigurationDialogVisibility = visible;
  }

  public showEditClassifiersConfigurationDialog(item: ClassifiersConfigurationDto): void {
    this.editClassifiersConfiguration = new ClassifiersConfigurationModel(OperationType.EDIT, item.id, item.configurationName);
    this.editClassifiersConfigurationDialogVisibility = true;
  }

  public showCopyClassifiersConfigurationDialog(item: ClassifiersConfigurationDto): void {
    this.editClassifiersConfiguration = new ClassifiersConfigurationModel(OperationType.COPY, item.id, item.configurationName);
    this.editClassifiersConfigurationDialogVisibility = true;
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

  public onDownloadReport(item: ClassifiersConfigurationDto): void {
    const observable = this.classifiersConfigurationService.getClassifiersConfigurationReport(item.id);
    this.downloadReport(observable, Utils.getClassifiersConfigurationFile(item));
  }

  public onChooseClassifierOptionsTemplate(template: FormTemplateDto): void {
    this.selectedTemplate = template;
    this.selectedFormFields = this.formTemplatesMapper.mapToFormFields(template.fields);
    this.addClassifiersOptionsDialogVisibility = true;
  }

  public onSetActiveClassifiersConfiguration(item: ClassifiersConfigurationDto): void {
    this.setActiveConfiguration(item);
  }

  public onUploadedClassifiersOptions(event): void {
    this.getClassifiersConfigurationDetails();
    this.reloadPageWithLoader();
  }

  public onEditClassifiersConfiguration(item: ClassifiersConfigurationModel): void {
    switch (item.operation) {
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

  public onAddClassifierOptionsDialogVisibility(visible): void {
    this.addClassifiersOptionsDialogVisibility = visible;
  }

  public onAddClassifierOptions(formFields: FormField[]): void {
    const classifierOptions = this.formTemplatesMapper.mapToClassifierOptionsObject(formFields, this.selectedTemplate);
    this.addClassifiersOptions(classifierOptions);
  }

  private deleteConfiguration(item: ClassifiersConfigurationDto): void {
    this.classifiersConfigurationService.deleteConfiguration(item.id)
      .subscribe({
        next: () => {
          localStorage.setItem(ExperimentTabUtils.EXPERIMENT_ACTIVE_TAB_KEY, ExperimentTabUtils.CLASSIFIERS_CONFIGURATION_TAB_INDEX.toString());
          this.router.navigate(['/dashboard/experiments']);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private setActiveConfiguration(item: ClassifiersConfigurationDto): void {
    this.classifiersConfigurationService.setActive(item.id)
      .subscribe({
        next: () => {
          this.getClassifiersConfigurationDetails();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private updateConfiguration(item: ClassifiersConfigurationModel): void {
    this.classifiersConfigurationService
      .updateConfiguration({ id: item.id, configurationName: item.configurationName })
      .subscribe({
        next: () => {
          this.getClassifiersConfigurationDetails();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private copyConfiguration(item: ClassifiersConfigurationModel): void {
    this.classifiersConfigurationService
      .copyConfiguration({ id: item.id, configurationName: item.configurationName })
      .subscribe({
        next: (configuration: ClassifiersConfigurationDto) => {
          this.messageService.add({ severity: 'success',
            summary: `Создана копия конфигурации с именем ${configuration.configurationName}`, detail: '' });
          this.getClassifiersConfigurationDetails();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private deleteClassifierOptions(item: ClassifierOptionsDto): void {
    this.loading = true;
    this.classifierOptionsService.deleteClassifierOptions(item.id)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: () => {
          this.messageService.add({ severity: 'success', summary: `Удалены настройки классификатора "${item.optionsDescription}"`, detail: '' });
          this.getClassifiersConfigurationDetails();
          this.reloadPageWithLoader();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private addClassifiersOptions(classifierOptions: any): void {
    this.loading = true;
    this.classifierOptionsService.addClassifiersOptions(this.configurationId, classifierOptions)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (classifierOptionsDto: ClassifierOptionsDto) => {
          this.lastCreatedId = classifierOptionsDto.id;
          this.messageService.add({ severity: 'success',
            summary: `Добавлены настройки классификатора "${classifierOptionsDto.optionsDescription}"`, detail: '' });
          this.getClassifiersConfigurationDetails();
          this.reloadPageWithLoader();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public hasMoreThanOneOptionsForActiveConfiguration(): boolean {
    return !this.classifiersConfiguration.active || this.items.length > 1;
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

  private initColumns() {
    this.columns = [
      { name: ClassifierOptionsFields.ID, label: "#" },
      { name: ClassifierOptionsFields.OPTIONS_DESCRIPTION, label: "Настройки классификатора" },
      { name: ClassifierOptionsFields.CREATION_DATE, label: "Дата создания настроек" },
      { name: ClassifierOptionsFields.CREATED_BY, label: "Пользователь" }
    ];
  }
}

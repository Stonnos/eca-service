import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifierOptionsDto, ClassifiersConfigurationDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ClassifierOptionsService } from "../services/classifier-options.service";
import { ConfirmationService, MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { OverlayPanel} from "primeng/primeng";
import { JsonPipe } from "@angular/common";
import { Observable } from "rxjs/internal/Observable";
import { ClassifierOptionsFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { ActivatedRoute, Router } from "@angular/router";
import { ClassifiersConfigurationsService } from "../../classifiers-configurations/services/classifiers-configurations.service";
import { ClassifiersConfigurationModel } from "../../create-classifiers-configuration/model/classifiers-configuration.model";
import { ExperimentTabUtils } from "../../experiments-tabs/model/experiment-tab.utils";
import {finalize} from "rxjs/internal/operators";

declare var Prism: any;

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

  public constructor(private injector: Injector,
                     private classifierOptionsService: ClassifierOptionsService,
                     private classifiersConfigurationService: ClassifiersConfigurationsService,
                     private route: ActivatedRoute,
                     private confirmationService: ConfirmationService,
                     private router: Router) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.configurationId = this.route.snapshot.params.id;
    this.defaultSortField = ClassifierOptionsFields.CREATION_DATE;
    this.linkColumns = [ClassifierOptionsFields.OPTIONS_NAME];
    this.initColumns();
  }

  public ngOnInit() {
    this.getClassifiersConfigurationDetails();
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

  public getFormattedJsonConfig(): string {
    const configObj = JSON.parse(this.selectedOptions.config);
    const json = new JsonPipe().transform(configObj);
    return Prism.highlight(json, Prism.languages['json']);
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
    this.editClassifiersConfiguration = new ClassifiersConfigurationModel(item.id, item.configurationName);
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

  public onSetActiveClassifiersConfiguration(item: ClassifiersConfigurationDto): void {
    this.setActiveConfiguration(item);
  }

  public onUploadedClassifiersOptions(event): void {
    this.getClassifiersConfigurationDetails();
    this.refreshClassifiersOptionsPage();
  }

  public onEditClassifiersConfiguration(item: ClassifiersConfigurationModel): void {
    this.updateConfiguration(item);
  }

  private refreshClassifiersOptionsPage(): void {
    this.performPageRequest(0, this.pageSize, this.table.sortField, this.table.sortOrder == -1);
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
          this.messageService.add({ severity: 'success', summary: `Удалена настройка ${item.optionsName}`, detail: '' });
          this.refreshClassifiersOptionsPage();
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public hasMoreThanOneOptionsForActiveConfiguration(): boolean {
    return !this.classifiersConfiguration.active || this.items.length > 1;
  }

  private initColumns() {
    this.columns = [
      { name: ClassifierOptionsFields.OPTIONS_NAME, label: "Настройки классификатора" },
      { name: ClassifierOptionsFields.CREATION_DATE, label: "Дата создания настроек" }
    ];
  }
}

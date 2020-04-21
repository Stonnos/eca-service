import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifierOptionsDto, ClassifiersConfigurationDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ClassifierOptionsService } from "../services/classifier-options.service";
import { MessageService } from "primeng/api";
import { BaseListComponent } from "../../common/lists/base-list.component";
import { OverlayPanel} from "primeng/primeng";
import { JsonPipe } from "@angular/common";
import { Observable } from "rxjs/internal/Observable";
import { ClassifierOptionsFields } from "../../common/util/field-names";
import { FieldService } from "../../common/services/field.service";
import { ActivatedRoute } from "@angular/router";
import { ClassifiersConfigurationsService } from "../../classifiers-configurations/services/classifiers-configurations.service";

declare var Prism: any;

@Component({
  selector: 'app-classifiers-configuration-details',
  templateUrl: './classifiers-configuration-details.component.html',
  styleUrls: ['./classifiers-configuration-details.component.scss']
})
export class ClassifiersConfigurationDetailsComponent extends BaseListComponent<ClassifierOptionsDto> implements OnInit {

  private readonly configurationId: number;

  public classifiersConfiguration: ClassifiersConfigurationDto;

  public selectedOptions: ClassifierOptionsDto;

  public constructor(private injector: Injector,
                     private classifierOptionsService: ClassifierOptionsService,
                     private classifiersConfigurationService: ClassifiersConfigurationsService,
                     private route: ActivatedRoute) {
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

  private initColumns() {
    this.columns = [
      { name: ClassifierOptionsFields.OPTIONS_NAME, label: "Настройки классификатора" },
      { name: ClassifierOptionsFields.CREATION_DATE, label: "Дата создания настроек" }
    ];
  }
}

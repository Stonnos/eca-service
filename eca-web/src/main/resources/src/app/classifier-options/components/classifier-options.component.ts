import { Component, Injector, OnInit } from '@angular/core';
import {
  ClassifierOptionsDto, PageDto,
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

declare var Prism: any;

@Component({
  selector: 'app-classifier-options',
  templateUrl: './classifier-options.component.html',
  styleUrls: ['./classifier-options.component.scss']
})
export class ClassifierOptionsComponent extends BaseListComponent<ClassifierOptionsDto> implements OnInit {

  public selectedOptions: ClassifierOptionsDto;

  public constructor(private injector: Injector,
                     private classifierOptionsService: ClassifierOptionsService) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = ClassifierOptionsFields.CREATION_DATE;
    this.linkColumns = [ClassifierOptionsFields.OPTIONS_NAME];
    this.initColumns();
  }

  public ngOnInit() {
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsDto>> {
    return this.classifierOptionsService.getClassifiersOptions(pageRequest);
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
      { name: ClassifierOptionsFields.VERSION, label: "Версия настроек" },
      { name: ClassifierOptionsFields.CREATION_DATE, label: "Дата создания настроек" }
    ];
  }
}

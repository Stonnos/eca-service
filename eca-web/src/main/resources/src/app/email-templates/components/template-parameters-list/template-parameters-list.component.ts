import { Component, Input, OnInit } from '@angular/core';
import { EmailTemplateParameterDto } from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { EmailTemplateParameterFields } from "../../../common/util/field-names";

@Component({
  selector: 'app-template-parameters-list',
  templateUrl: './template-parameters-list.component.html',
  styleUrls: ['./template-parameters-list.scss']
})
export class TemplateParametersListComponent implements OnInit {

  public columns: any[] = [];
  public caption: string = 'Переменные шаблона';

  @Input()
  public items: EmailTemplateParameterDto[] = [];

  public constructor() {
    this.initColumns();
  }

  public ngOnInit() {
  }

  private initColumns() {
    this.columns = [
      { name: EmailTemplateParameterFields.PARAMETER_NAME, label: "Имя переменной" },
      { name: EmailTemplateParameterFields.DESCRIPTION, label: "Описание переменной" },
      { name: EmailTemplateParameterFields.CREATED, label: "Дата создания" }
    ];
  }
}

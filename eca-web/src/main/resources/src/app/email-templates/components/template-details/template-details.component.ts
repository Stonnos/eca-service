import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { EmailTemplateFields } from "../../../common/util/field-names";
import { EmailTemplateDto } from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseDialog } from "../../../common/dialog/base-dialog";

@Component({
  selector: 'app-template-details',
  templateUrl: './template-details.component.html',
  styleUrls: ['./template-details.component.scss']
})
export class TemplateDetailsComponent implements BaseDialog, OnInit {

  @Input()
  public visible: boolean = false;
  @Output()
  public visibilityChange: EventEmitter<boolean> = new EventEmitter();

  @Input()
  public templateText: string;
  @Input()
  public template: EmailTemplateDto;

  public templateFields: any[] = [];

  public constructor() {
    this.initTemplateFields();
  }

  public ngOnInit(): void {
  }

  public hide(): void {
    this.visibilityChange.emit(false);
  }

  public getTemplateValue(fieldName: string): any {
    return this.template && this.template[fieldName];
  }

  private initTemplateFields(): void {
    this.templateFields = [
      { name: EmailTemplateFields.CODE, label: "Код шаблона:" },
      { name: EmailTemplateFields.DESCRIPTION, label: "Описание шаблона:" },
      { name: EmailTemplateFields.SUBJECT, label: "Тема письма:" },
      { name: EmailTemplateFields.CREATED, label: "Дата создания:" }
    ];
  }
}

import { Component, Injector } from '@angular/core';
import { MessageService } from "primeng/api";
import { Observable } from "rxjs/internal/Observable";
import {
  EmailTemplateDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseListComponent } from "../../../common/lists/base-list.component";
import { FieldService } from "../../../common/services/field.service";
import { EmailTemplateFields } from "../../../common/util/field-names";
import { EmailTemplatesService } from "../../services/email-templates.service";

@Component({
  selector: 'app-templates-list',
  templateUrl: './templates-list.component.html',
  styleUrls: ['./templates-list.component.scss']
})
export class TemplatesListComponent extends BaseListComponent<EmailTemplateDto> {

  public constructor(private injector: Injector,
                     private emailTemplatesService: EmailTemplatesService) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = EmailTemplateFields.CREATED;
    this.initColumns();
  }

  public ngOnInit() {
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<EmailTemplateDto>> {
    return this.emailTemplatesService.getTemplates(pageRequest);
  }

  private initColumns() {
    this.columns = [
      { name: EmailTemplateFields.CODE, label: "Код шаблона" },
      { name: EmailTemplateFields.DESCRIPTION, label: "Описание шаблона" },
      { name: EmailTemplateFields.SUBJECT, label: "Тема письма" },
      { name: EmailTemplateFields.CREATED, label: "Дата создания" }
    ];
  }
}

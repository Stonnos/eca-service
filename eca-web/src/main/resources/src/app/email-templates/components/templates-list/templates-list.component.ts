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
import { EmailTemplateFilterFields } from "../../../common/util/filter-field-names";

@Component({
  selector: 'app-templates-list',
  templateUrl: './templates-list.component.html',
  styleUrls: ['./templates-list.component.scss']
})
export class TemplatesListComponent extends BaseListComponent<EmailTemplateDto> {

  public templateDetailsVisibility: boolean = false;
  public template: EmailTemplateDto;

  public constructor(private injector: Injector,
                     private emailTemplatesService: EmailTemplatesService) {
    super(injector.get(MessageService), injector.get(FieldService));
    this.defaultSortField = EmailTemplateFields.CREATED;
    this.linkColumns = [EmailTemplateFields.CODE];
    this.initColumns();
  }

  public ngOnInit() {
  }

  public getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<EmailTemplateDto>> {
    return this.emailTemplatesService.getTemplates(pageRequest);
  }

  public onSelect(item: EmailTemplateDto, column: string): void {
    if (column == EmailTemplateFields.CODE) {
      this.template = item;
      this.templateDetailsVisibility = true;
    } else {
      this.messageService.add({severity: 'error', summary: 'Ошибка', detail: `Can't handle ${column} as link`});
    }
  }

  public onHide(): void {
    this.templateDetailsVisibility = false;
  }

  private initColumns() {
    this.columns = [
      { name: EmailTemplateFields.CODE, label: "Код шаблона", sortBy: EmailTemplateFilterFields.CODE },
      { name: EmailTemplateFields.DESCRIPTION, label: "Описание шаблона", sortBy: EmailTemplateFilterFields.DESCRIPTION },
      { name: EmailTemplateFields.SUBJECT, label: "Тема письма", sortBy: EmailTemplateFilterFields.SUBJECT },
      { name: EmailTemplateFields.CREATED, label: "Дата создания", sortBy: EmailTemplateFilterFields.CREATED }
    ];
  }
}

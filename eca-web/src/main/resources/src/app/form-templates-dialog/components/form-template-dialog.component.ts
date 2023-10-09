import { Component, Input, OnInit } from '@angular/core';
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { FormTemplateDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { FormField } from "../../form-templates/model/form-template.model";

@Component({
  selector: 'app-form-template-dialog',
  templateUrl: './form-template-dialog.component.html',
  styleUrls: ['./form-template-dialog.component.scss']
})
export class FormTemplateDialogComponent extends BaseCreateDialogComponent<FormField[]> implements OnInit {

  @Input()
  public template: FormTemplateDto;

  public ngOnInit(): void {
  }
}

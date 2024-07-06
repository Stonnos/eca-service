import { Component, Input, OnInit } from '@angular/core';
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { FormTemplateDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { FormField } from '../model/form-template.model';

@Component({
  selector: 'app-edit-template-item-dialog',
  templateUrl: './edit-template-item-dialog.component.html',
  styleUrls: ['./edit-template-item-dialog.component.scss']
})
export class EditTemplateItemDialogComponent extends BaseCreateDialogComponent<FormField[]> implements OnInit {

  @Input()
  public template: FormTemplateDto;

  public ngOnInit(): void {
  }
}

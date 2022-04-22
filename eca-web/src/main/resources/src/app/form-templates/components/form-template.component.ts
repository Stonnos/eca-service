import { Component, Input, OnInit } from '@angular/core';
import { FormField } from "../model/form-template.model";
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { FormTemplateDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-form-template',
  templateUrl: './form-template.component.html',
  styleUrls: ['./form-template.component.scss']
})
export class FormTemplateComponent extends BaseCreateDialogComponent<FormField[]> implements OnInit {

  private static readonly DEFAULT_PLACEHOLDER = 'Введите значение';

  @Input()
  public template: FormTemplateDto;

  public ngOnInit(): void {
  }

  public getPlaceholder(formField: FormField): string {
    if (formField.placeholder) {
      return formField.placeholder;
    }
    return FormTemplateComponent.DEFAULT_PLACEHOLDER;
  }

  public getValidationErrors(state: any, formField: FormField): string[] {
    let messages: string[] = [];
    if (state && state.errors) {
      for (let errorName in state.errors) {
        switch (errorName) {
          case 'required':
            messages.push(`Заполните поле`);
            break;
          case 'pattern':
            if (formField.invalidPatternMessage) {
              messages.push(formField.invalidPatternMessage);
            } else {
              messages.push(`Значение не соответствует паттерну ${formField.pattern}`);
            }
            break;
          case 'minNumber':
            messages.push(`Значение должно быть больше либо равно ${formField.min}`);
            break;
          case 'maxNumber':
            messages.push(`Значение должно быть меньше либо равно ${formField.max}`);
            break;
        }
      }
    }
    return messages;
  }
}

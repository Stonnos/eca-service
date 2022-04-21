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

  @Input()
  public template: FormTemplateDto;

  public ngOnInit(): void {
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
            messages.push(`Значение не соответствует паттерну ${formField.pattern}`);
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

  public submit() {
    this.submitted = true;
    let classifierOptions = { type: this.template.objectType };
    this.item.forEach((formField: FormField) => {
      const fields: string[] = formField.name.split(".");
      if (fields.length == 1) {
        classifierOptions[formField.name] = this.getValue(formField);
      } else {
        let current = classifierOptions;
        for (let i = 0; i < fields.length - 1; i++) {
          if (!current[fields[i]]) {
            current[fields[i]] = {};
          }
          current = current[fields[i]];
        }
        current[fields[fields.length - 1]] = this.getValue(formField);
      }
    });
    console.log(JSON.stringify(classifierOptions));
  }

  private getValue(formField: FormField): any {
    if (formField.fieldType == 'REFERENCE') {
      return formField.currentValue.value;
    } else {
      return formField.currentValue;
    }
  }
}

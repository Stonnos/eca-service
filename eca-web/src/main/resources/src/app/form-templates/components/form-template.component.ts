import { Component, Input, OnInit } from '@angular/core';
import { FormField } from "../model/form-template.model";
import { FormTemplateDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ControlContainer, NgForm } from "@angular/forms";

@Component({
  selector: 'app-form-template',
  templateUrl: './form-template.component.html',
  styleUrls: ['./form-template.component.scss'],
  // Allow template fields validation from parent component
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }]
})
export class FormTemplateComponent implements OnInit {

  private static readonly DEFAULT_PLACEHOLDER = 'Введите значение';

  @Input()
  public template: FormTemplateDto;
  @Input()
  public formFields: FormField[] = [];
  @Input()
  public submitted: boolean = false;

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
            if (formField.minInclusive) {
              messages.push(`Значение должно быть больше либо равно ${formField.min}`);
            } else {
              messages.push(`Значение должно быть больше ${formField.min}`);
            }
            break;
          case 'maxNumber':
            if (formField.maxInclusive) {
              messages.push(`Значение должно быть меньше либо равно ${formField.max}`);
            } else {
              messages.push(`Значение должно быть меньше ${formField.max}`);
            }
            break;
        }
      }
    }
    return messages;
  }
}

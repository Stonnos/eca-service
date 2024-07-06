import { Component, Input, OnInit } from '@angular/core';
import { FormField, FormTemplate, ObjectItem } from '../model/form-template.model';
import { FormTemplateDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ControlContainer, NgForm } from "@angular/forms";
import { SelectItem } from 'primeng/api';
import { FormFieldType } from '../../common/model/form-field-type.enum';

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

  public selectedTemplateToEdit: FormTemplateDto;
  public selectedFormFieldsToEdit: FormField[] = [];

  public ngOnInit(): void {
  }

  public getPlaceholder(formField: FormField): string {
    if (formField.placeholder) {
      return formField.placeholder;
    }
    return FormTemplateComponent.DEFAULT_PLACEHOLDER;
  }

  public onEditObjectItemVisibilityChange(visible, formField: FormField): void {
    formField.editObjectItemVisibility = visible;
  }

  public onAddObjectItem(event, formField: FormField) {
    formField.nextItemIndex = formField.nextItemIndex + 1;
    let templateCopy = new FormTemplate(formField.selectedItem.label, formField.selectedItem.value.template.template, this.copyFormFields(formField.selectedItem.value.template.fields));
    let item: ObjectItem = new ObjectItem(formField.nextItemIndex, templateCopy);
    formField.currentValue = [...formField.currentValue, {
      label: formField.selectedItem.label,
      value: item
    }];
  }

  public isMaxObjectItems(formField: FormField): boolean {
    return formField.currentValue && formField.currentValue.length >= formField.maxLength;
  }

  public onObjectItemChange(event, formField: FormField) {
    if (!event.value) {
      formField.selectedItemToEdit = null;
    } else {
      formField.selectedItemToEdit = event.value.value;
    }
  }

  public onStartEditObjectListItem(event, formField: FormField) {
    formField.selectedItemToEdit = event.value.value;
    this.selectedTemplateToEdit = event.value.value.template.template;
    this.selectedFormFieldsToEdit = this.copyFormFields(event.value.value.template.fields);
    formField.editObjectItemVisibility = true;
  }

  public onFinishEditObjectListItem(formFields: FormField[], formField: FormField) {
    let indexToEdit = formField.selectedItemToEdit.index;
    formField.currentValue.filter((value: SelectItem) => value.value.index == indexToEdit).forEach((value: SelectItem) => {
      value.value.template.fields = this.copyFormFields(formFields);
    });
  }

  public onStartEditObjectItem(event, formField: FormField) {
    this.selectedTemplateToEdit = formField.currentValue.value.template.template;
    this.selectedFormFieldsToEdit = this.copyFormFields(formField.currentValue.value.template.fields);
    formField.editObjectItemVisibility = true;
  }

  public onFinishEditObjectItem(formFields: FormField[], formField: FormField) {
    formField.currentValue.value.template.fields = this.copyFormFields(formFields);
  }

  public isValidAllAdditionalFields(): boolean {
    for (let i = 0; i < this.formFields.length; i++) {
      if (!this.isValidAdditionalFields(this.formFields[i])) {
        return false;
      }
    }
    return true;
  }

  public isValidAdditionalFields(formField: FormField): boolean {
    if (formField.fieldType == FormFieldType.LIST_OBJECTS) {
      return formField.currentValue && formField.currentValue.length > 0;
    } else {
      return true;
    }
  }

  public onDeleteObjectItem(event, formField: FormField) {
    formField.currentValue = formField.currentValue.filter((val: any) => val.value.index != formField.selectedItemToEdit.index);
    this.selectedTemplateToEdit = null;
    this.selectedFormFieldsToEdit = [];
    formField.selectedItemToEdit = null;
  }

  public copyFormFields(fields: FormField[]): FormField[] {
    return fields.map((field: FormField) => {
      let copy: FormField = new FormField(field.name, field.label, field.fieldType);
      copy.maxLength = field.maxLength;
      copy.min = field.min;
      copy.minInclusive = field.minInclusive;
      copy.max = field.max;
      copy.maxInclusive = field.minInclusive;
      copy.pattern = field.pattern;
      copy.defaultValue = field.defaultValue;
      copy.invalidPatternMessage = field.invalidPatternMessage;
      copy.placeholder = field.placeholder;
      copy.values = field.values.map((value: SelectItem) => {
        return { label: value.label, value: value.value };
      });
      copy.currentValue = field.currentValue;
      return copy;
    });
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

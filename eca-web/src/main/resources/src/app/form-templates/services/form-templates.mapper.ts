import { Injectable } from '@angular/core';
import {
  FilterDictionaryValueDto,
  FormFieldDto, FormTemplateDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { SelectItem } from "primeng/api";
import { FormField, FormTemplate, ObjectItem } from '../model/form-template.model';
import { FormFieldType } from '../../common/model/form-field-type.enum';

@Injectable()
export class FormTemplatesMapper {

  public mapToFormFields(formFields: FormFieldDto[]): FormField[] {
    return formFields.filter((formFieldDto: FormFieldDto) => !formFieldDto.readOnly).map((formFieldDto: FormFieldDto) => {
      const formField = new FormField(formFieldDto.fieldName, formFieldDto.description, formFieldDto.fieldType);
      formField.maxLength = formFieldDto.maxLength;
      formField.min = formFieldDto.minValue;
      formField.minInclusive = formFieldDto.minInclusive;
      formField.max = formFieldDto.maxValue;
      formField.maxInclusive = formFieldDto.maxInclusive;
      formField.pattern = formFieldDto.pattern;
      formField.invalidPatternMessage = formFieldDto.invalidPatternMessage;
      formField.invalidRequiredMessage = formFieldDto.invalidRequiredMessage;
      formField.invalidMaxLengthMessage = formFieldDto.invalidMaxLengthMessage;
      formField.placeholder = formFieldDto.placeHolder;
      formField.defaultValue = formFieldDto.defaultValue;
      if (formFieldDto.fieldType == FormFieldType.REFERENCE && formFieldDto.dictionary &&
        formFieldDto.dictionary.values && formFieldDto.dictionary.values.length > 0) {
        formField.values = formFieldDto.dictionary.values.map((filterValue: FilterDictionaryValueDto) => {
          return { label: filterValue.label, value: filterValue.value };
        });
      }
      if (formFieldDto.fieldType == FormFieldType.LIST_OBJECTS) {
        formField.currentValue = [];
        this.mapTemplateValues(formField, formFieldDto);
      }
      if (formFieldDto.fieldType == FormFieldType.ONE_OF_OBJECT) {
        this.mapTemplateValues(formField, formFieldDto);
      }
      if (formFieldDto.defaultValue) {
        this.setCurrentValue(formField, formFieldDto);
      }
      return formField;
    });
  }

  public mapToClassifierOptionsObject(formFields: FormField[], formTemplateDto: FormTemplateDto): any {
    let classifierOptions = this.mapToObject(formFields);
    classifierOptions['type'] = formTemplateDto.objectType;
    return classifierOptions;
  }

  public mapToObject(formFields: FormField[]): any {
    let object = {};
    formFields.forEach((formField: FormField) => {
      const fields: string[] = formField.name.split(".");
      if (fields.length == 1) {
        object[formField.name] = this.getCurrentValue(formField);
      } else {
        let currentProperty = object;
        for (let i = 0; i < fields.length - 1; i++) {
          if (!currentProperty[fields[i]]) {
            currentProperty[fields[i]] = {};
          }
          currentProperty = currentProperty[fields[i]];
        }
        currentProperty[fields[fields.length - 1]] = this.getCurrentValue(formField);
      }
    });
    return object;
  }

  private mapTemplateValues(formField: FormField, formFieldDto: FormFieldDto): void {
    formField.values = formFieldDto.formTemplateGroup.templates.map((template: FormTemplateDto, index: number) => {
      const fields = this.mapToFormFields(template.fields);
      let formTemplate = new FormTemplate(template.templateTitle, template, fields);
      let item: ObjectItem = new ObjectItem(index, formTemplate);
      return {
        label: template.templateTitle,
        value: item
      };
    });
  }

  private setCurrentValue(formField: FormField, formFieldDto: FormFieldDto): void {
    switch (formFieldDto.fieldType) {
      case "REFERENCE":
        formField.currentValue = formField.values.find((item: SelectItem) => item.value === formField.defaultValue);
        break;
      case "INTEGER":
        formField.currentValue = parseInt(formFieldDto.defaultValue);
        break;
      case "DECIMAL":
        formField.currentValue = parseFloat(formFieldDto.defaultValue);
        break;
      case "BOOLEAN":
        formField.currentValue = formFieldDto.defaultValue === 'true';
        break;
      default:
        formField.currentValue = formFieldDto.defaultValue;
    }
  }

  private getCurrentValue(formField: FormField): any {
    if (formField.fieldType == 'REFERENCE') {
      return formField.currentValue.value;
    } else if (formField.fieldType == 'LIST_OBJECTS') {
      return formField.currentValue.map(value => this.mapToClassifierOptionsObject(value.value.template.fields, value.value.template.template));
    } else if (formField.fieldType == 'ONE_OF_OBJECT') {
      return this.mapToClassifierOptionsObject(formField.currentValue.value.template.fields, formField.currentValue.value.template.template)
    } else {
      return formField.currentValue;
    }
  }
}

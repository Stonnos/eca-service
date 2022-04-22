import { Injectable } from '@angular/core';
import {
  FilterDictionaryValueDto,
  FormFieldDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { SelectItem } from "primeng/api";
import { FilterFieldType } from "../../common/model/filter-field-type.enum";
import { FormField } from "../model/form-template.model";

@Injectable()
export class FormTemplatesMapper {

  public mapToFormFields(formFields: FormFieldDto[]): FormField[] {
    return formFields.map((formFieldDto: FormFieldDto) => {
      const formField = new FormField(formFieldDto.fieldName, formFieldDto.description, formFieldDto.fieldType);
      formField.maxLength = formFieldDto.maxLength;
      formField.min = formFieldDto.minValue;
      formField.max = formFieldDto.maxValue;
      formField.pattern = formFieldDto.pattern;
      formField.invalidPatternMessage = formFieldDto.invalidPatternMessage;
      formField.placeholder = formFieldDto.placeHolder;
      formField.defaultValue = formFieldDto.defaultValue;
      if (formFieldDto.fieldType == FilterFieldType.REFERENCE && formFieldDto.dictionary &&
        formFieldDto.dictionary.values && formFieldDto.dictionary.values.length > 0) {
        formField.values = formFieldDto.dictionary.values.map((filterValue: FilterDictionaryValueDto) => {
          return { label: filterValue.label, value: filterValue.value };
        });
      }
      if (formFieldDto.defaultValue) {
        this.setCurrentValue(formField, formFieldDto);
      }
      return formField;
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
        formFieldDto.defaultValue = 'true';
        formField.currentValue = Boolean(formFieldDto.defaultValue);
        break;
      default:
        formField.currentValue = formFieldDto.defaultValue;
    }
  }
}

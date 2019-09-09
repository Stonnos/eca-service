import { Injectable } from '@angular/core';

@Injectable()
export class FieldService {

  public getFieldValue(field: string, object: any, defaultValue?: any): any {
    const computedDefaultVal = defaultValue ? defaultValue : null;
    if (object) {
      const fields: string[] = field.split(".");
      return this.getValueInternal(fields, object, computedDefaultVal);
    }
    return computedDefaultVal;
  }

  public hasValue(field: string, object: any): boolean {
    return this.getFieldValue(field, object, null) != null;
  }

  private getValueInternal(fields: string[], object: any, defaultValue: any): any {
    let value = object;
    for (let i = 0; i < fields.length; i++) {
      value = value[fields[i]];
      if (value == null) {
        return defaultValue;
      }
    }
    return value;
  }
}

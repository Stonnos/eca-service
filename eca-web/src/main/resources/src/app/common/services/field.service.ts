import { Injectable } from '@angular/core';

@Injectable()
export class FieldService {

  public getFieldValue(field: string, object: any, defaultValue?: any): any {
    const computedDefaultVal = defaultValue ? defaultValue : null;
    if (object) {
      const fields: string[] = field.split(".");
      return this.getValue(fields, object, computedDefaultVal);
    }
    return computedDefaultVal;
  }

  private getValue(fields: string[], object: any, defaultValue: any): any {
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

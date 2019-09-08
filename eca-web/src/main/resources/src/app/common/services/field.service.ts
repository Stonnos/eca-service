import { Injectable } from '@angular/core';

@Injectable()
export class FieldService {

  public getFieldValue(field: string, object: any): any {
    if (object) {
      const fields: string[] = field.split(".");
      return this.getValue(fields, object);
    }
    return null;
  }

  private getValue(fields: string[], object: any): any {
    let value = object;
    fields.forEach((field) => {
      value = value[field];
      if (!value) {
        return null;
      }
    });
    return value;
  }
}

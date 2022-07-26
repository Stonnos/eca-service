import { FieldService } from "../services/field.service";
import { Input } from "@angular/core";
import { Utils } from "../util/utils";

export abstract class BaseDetailsComponent<T> {

  public fields: any[] = [];

  @Input()
  public item: T;

  protected constructor(public fieldService: FieldService) {
  }

  public getFieldValue(field: string) {
    return this.fieldService.getFieldValue(field, this.item, Utils.MISSING_VALUE);
  }
}

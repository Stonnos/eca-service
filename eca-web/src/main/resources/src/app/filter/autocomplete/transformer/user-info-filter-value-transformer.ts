import { LazyReferenceFilterValueTransformer } from "./lazy-reference-filter-value-transformer";
import { Filter } from "../../model/filter.model";

export class UserInfoFilterValueTransformer extends LazyReferenceFilterValueTransformer {

  public constructor(filterField: string) {
    super(filterField);
  }

  public transformValues(filter: Filter, values: any[]): string[] {
    return values.map((item) => item.value.login);
  }
}

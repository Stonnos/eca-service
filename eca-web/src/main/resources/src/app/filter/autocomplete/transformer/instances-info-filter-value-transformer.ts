import { LazyReferenceFilterValueTransformer } from "./lazy-reference-filter-value-transformer";
import { Filter } from "../../model/filter.model";

export class InstancesInfoFilterValueTransformer extends LazyReferenceFilterValueTransformer {

  public constructor() {
    super('instancesInfo.id');
  }

  public transformValues(filter: Filter, values: any[]): string[] {
    return values.map((item) => item.value.id);
  }
}

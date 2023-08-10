import { Filter } from "../../model/filter.model";

export abstract class LazyReferenceFilterValueTransformer {

  private readonly filterField: string;

  protected constructor(filterField: string) {
    this.filterField = filterField;
  }

  public getFilterField(): string {
    return this.filterField;
  }

  public abstract transformValues(filter: Filter, values: any[]): string[];
}


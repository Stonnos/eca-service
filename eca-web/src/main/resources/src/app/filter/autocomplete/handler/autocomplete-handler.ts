import { AutocompleteItemModel } from "../../model/autocomplete-item.model";
import { Filter } from "../../model/filter.model";


export abstract class AutocompleteHandler {

  private readonly filterField: string;

  protected constructor(filterField: string) {
    this.filterField = filterField;
  }

  public getFilterField(): string {
    return this.filterField;
  }

  public abstract onFilterFieldAutocomplete(filters: Filter[], autocompleteItemModel: AutocompleteItemModel): void;
}

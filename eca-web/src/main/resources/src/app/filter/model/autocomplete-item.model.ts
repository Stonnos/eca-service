export class AutocompleteItemModel {
  filterField: string;
  searchQuery: string;

  public constructor(filterField: string, searchQuery: string) {
    this.filterField = filterField;
    this.searchQuery = searchQuery;
  }
}

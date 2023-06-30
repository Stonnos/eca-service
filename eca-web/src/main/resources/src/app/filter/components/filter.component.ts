import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Filter } from "../model/filter.model";
import { AutocompleteItemModel } from "../model/autocomplete-item.model";

@Component({
  selector: 'app-filter',
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.scss']
})
export class FilterComponent {

  public now: Date = new Date();

  public selectedAutocompleteField: string;

  @Input()
  public filters: Filter[];

  @Output()
  public apply: EventEmitter<void> = new EventEmitter();

  @Output()
  public autocompleteField: EventEmitter<AutocompleteItemModel> = new EventEmitter<AutocompleteItemModel>();

  public onApply() {
    this.apply.emit();
  }

  public resetFilter() {
    this.filters.forEach(filter => {
      filter.currentValue = null;
      filter.currentValues = null;
    });
    this.apply.emit();
  }

  public onAutocompleteItem(fieldName: string, event): void {
    this.selectedAutocompleteField = fieldName;
    this.autocompleteField.emit(new AutocompleteItemModel(fieldName, event.query));
  }
}

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Filter } from "../model/filter.model";

@Component({
  selector: 'app-filter',
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.scss']
})
export class FilterComponent {

  public now: Date = new Date();

  @Input()
  public filters: Filter[];

  @Output()
  public apply: EventEmitter<void> = new EventEmitter();

  public onApply() {
    this.apply.emit();
  }

  public resetFilter() {
    this.filters.forEach(filter => filter.currentValue = null);
  }
}

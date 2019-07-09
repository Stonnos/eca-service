import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Filter } from "../model/filter.model";
import { MatchMode } from "../../common/model/match-mode.enum";

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
    this.filters.forEach(filter => {
      filter.currentValue = null;
      filter.currentValues = null;
    });
  }

  public getDateSelectionMode(filter: Filter): string {
    if (filter.multiple) {
      if (filter.matchMode == MatchMode.RANGE) {
        return 'range';
      } else if (filter.matchMode == MatchMode.EQUALS) {
        return 'multiple';
      }
    }
    return 'single';
  }
}

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Filter } from "../filter.model";

@Component({
  selector: 'app-filter',
  templateUrl: './filter.component.html',
  styleUrls: ['./filter.component.scss']
})
export class FilterComponent {

  @Input()
  public filters: Filter[];

  @Output()
  public search: EventEmitter<void> = new EventEmitter();

  public onSearch() {
    this.search.emit();
  }
}

import { Component, Input, OnInit, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-search-query',
  templateUrl: './search-query.component.html',
  styleUrls: ['./search-query.component.scss']
})
export class SearchQueryComponent implements OnInit {

  @Input()
  public minQuerySize: number = 1;

  @Input()
  public searchQuery: string = '';

  @Output()
  public search: EventEmitter<string> = new EventEmitter<string>();

  public ngOnInit() {
  }

  public onSearch() {
    if (this.searchQuery.length == 0 || this.searchQuery.length >= this.minQuerySize) {
      this.search.emit(this.searchQuery);
    }
  }

  public clearSearchQuery(): void {
    this.searchQuery = '';
    this.onSearch();
  }
}

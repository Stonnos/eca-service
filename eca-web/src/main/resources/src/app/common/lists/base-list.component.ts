import { LazyLoadEvent, MessageService } from "primeng/api";
import {
  FilterRequestDto,
  PageDto,
  PageRequestDto
} from "../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Filter } from "../filter/filter.model";
import { DatePipe } from "@angular/common";
import { Table } from "primeng/table";
import { ViewChild } from "@angular/core";
import { Observable } from "rxjs/internal/Observable";
import { finalize } from "rxjs/internal/operators";

export abstract class BaseListComponent<T> {

  private static readonly MIN_QUERY_SIZE: number = 3;

  public defaultSortField: string;
  public defaultSortOrder: number = -1;
  public pageSize: number = 25;

  public searchQuery: string = '';
  public filters: Filter[] = [];
  public columns: any[] = [];
  public linkColumns: string[] = [];
  public notSortableColumns: string[] = [];
  public items: T[] = [];
  public loading: boolean = true;

  @ViewChild(Table)
  private table: Table;

  private filterRequests: FilterRequestDto[] = [];

  private dateFormat: string = "yyyy-MM-dd";

  protected constructor(public messageService: MessageService) {
  }

  public getNextPage(pageRequest: PageRequestDto) {
    this.loading = true;
    this.getNextPageAsObservable(pageRequest)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((pageDto: PageDto<T>) => {
        this.setPage(pageDto);
      }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }

  public abstract getNextPageAsObservable(pageRequest: PageRequestDto): Observable<PageDto<T>>;

  public onLazyLoad(event: LazyLoadEvent) {
    const page: number = Math.round(event.first / event.rows);
    this.performPageRequest(page, event.rows, event.sortField, event.sortOrder == 1);
  }

  public onSearch() {
    if (this.searchQuery.length == 0 || this.searchQuery.length >= BaseListComponent.MIN_QUERY_SIZE) {
      this.performPageRequest(0, this.pageSize, this.table.sortField, this.table.sortOrder == 1);
    }
  }

  public onApplyFilter() {
    this.rebuildFilterRequests();
    this.performPageRequest(0, this.pageSize, this.table.sortField, this.table.sortOrder == 1);
  }

  public setPage(pageDto: PageDto<T>) {
    this.items = pageDto.content;
    this.table.totalRecords = pageDto.totalCount;
    if (pageDto.page == 0) {
      this.table.first = 0;
    }
  }

  public isLink(column: string): boolean {
    return this.linkColumns.includes(column);
  }

  public notSortable(column: string): boolean {
    return this.notSortableColumns.includes(column);
  }

  public clearSearchQuery(): void {
    this.searchQuery = '';
    this.onSearch();
  }

  private performPageRequest(page: number, size: number, sortField: string, ascending: boolean) {
    const pageRequest: PageRequestDto = {
      page: page,
      size: size,
      sortField: sortField,
      ascending: ascending,
      searchQuery: this.searchQuery,
      filters: this.filterRequests
    };
    this.getNextPage(pageRequest);
  }

  private rebuildFilterRequests(): void {
    this.filterRequests = this.filters.filter((filter: Filter) => this.hasValue(filter)).map((filter: Filter) => {
      return { name: filter.name, value: this.transformFilterValue(filter), filterFieldType: filter.filterFieldType, matchMode: filter.matchMode };
    });
  }

  private hasValue(filter: Filter): boolean {
    switch (filter.filterFieldType) {
      case "REFERENCE":
        return !!filter.currentValue && !!filter.currentValue.value;
      default:
        return !!filter.currentValue;
    }
  }

  private transformFilterValue(filter: Filter): string {
    switch (filter.filterFieldType) {
      case "DATE":
        return new DatePipe("en-US").transform(filter.currentValue, this.dateFormat);
      case "REFERENCE":
        return filter.currentValue.value;
      default:
        return filter.currentValue;
    }
  }
}

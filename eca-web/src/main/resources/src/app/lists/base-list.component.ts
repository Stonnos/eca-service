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

  public defaultSortField: string;
  public defaultSortOrder: number = -1;
  public pageSize: number = 25;

  public filters: Filter[] = [];
  public columns: any[] = [];
  public linkColumns: string[] = [];
  public notSortableColumns: string[] = [];
  public items: T[] = [];
  public loading: boolean = true;

  @ViewChild(Table)
  private table: Table;

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
    const pageRequest: PageRequestDto = {
      page: page,
      size: event.rows,
      sortField: event.sortField,
      ascending: event.sortOrder == 1,
      filters: this.buildFilters()
    };
    this.getNextPage(pageRequest);
  }

  public onSearch() {
    const pageRequest: PageRequestDto = {
      page: 0,
      size: this.pageSize,
      sortField: this.table.sortField,
      ascending: this.table.sortOrder == 1,
      filters: this.buildFilters()
    };
    this.getNextPage(pageRequest);
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

  private buildFilters(): FilterRequestDto[] {
    return this.filters.filter((filter: Filter) => this.hasValue(filter)).map((filter: Filter) => {
      return { name: filter.name, value: this.transformFilterValue(filter), filterType: filter.type, matchMode: filter.matchMode };
    });
  }

  private hasValue(filter: Filter): boolean {
    switch (filter.type) {
      case "REFERENCE":
        return !!filter.currentValue && !!filter.currentValue.value;
      default:
        return !!filter.currentValue;
    }
  }

  private transformFilterValue(filter: Filter): string {
    switch (filter.type) {
      case "DATE":
        return new DatePipe("en-US").transform(filter.currentValue, this.dateFormat);
      case "REFERENCE":
        return filter.currentValue.value;
      default:
        return filter.currentValue;
    }
  }
}

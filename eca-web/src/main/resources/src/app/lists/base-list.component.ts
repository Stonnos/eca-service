import { LazyLoadEvent } from "primeng/api";
import { FilterRequestDto, PageRequestDto } from "../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Filter } from "../filter/filter.model";
import { DatePipe } from "@angular/common";
import { Table } from "primeng/table";
import { ViewChild } from "@angular/core";

export abstract class BaseListComponent<T> {

  public defaultSortField: string;
  public defaultSortOrder: number = -1;
  public total: number = 0;
  public pageSize: number = 25;

  public filters: Filter[] = [];
  public columns: any[] = [];
  public linkColumns: string[] = [];
  public items: T[] = [];

  @ViewChild(Table)
  private table: Table;

  private dateFormat: string = "yyyy-MM-dd HH:mm:ss";

  public abstract getNextPage(pageRequest: PageRequestDto);

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
    this.resetSort();
    const pageRequest: PageRequestDto = {
      page: 0,
      size: this.pageSize,
      sortField: this.defaultSortField,
      ascending: this.defaultSortOrder == 1,
      filters: this.buildFilters()
    };
    this.getNextPage(pageRequest);
  }

  public isLink(column: string): boolean {
    return this.linkColumns.includes(column);
  }

  private buildFilters(): FilterRequestDto[] {
    return this.filters.filter((filter: Filter) => !!filter.currentValue).map((filter: Filter) => {
      return { name: filter.name, value: this.transformFilterValue(filter), filterType: filter.type, matchMode: filter.matchMode };
    });
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

  private resetSort() {
    this.table.sortField = this.defaultSortField;
    this.table.sortOrder = this.defaultSortOrder;
  }
}

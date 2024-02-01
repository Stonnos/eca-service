import {LazyLoadEvent, MessageService, SortMeta} from "primeng/api";
import { DatePipe } from "@angular/common";
import { Table } from "primeng/table";
import { ViewChild } from "@angular/core";
import { Observable } from "rxjs/internal/Observable";
import { finalize } from "rxjs/internal/operators";
import { Filter } from "../../filter/model/filter.model";
import {
  FilterRequestDto,
  PageDto, PageRequestDto,
  SimplePageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { FieldService } from "../services/field.service";
import { FieldLink } from "../model/field-link";
import { ColumnModel } from "../model/column.model";
import { saveAs } from 'file-saver/dist/FileSaver';
import { LazyReferenceFilterValueTransformer } from "../../filter/autocomplete/transformer/lazy-reference-filter-value-transformer";
import { AutocompleteHandler } from "../../filter/autocomplete/handler/autocomplete-handler";
import { AutocompleteItemModel } from "../../filter/model/autocomplete-item.model";

export abstract class BaseListComponent<T> implements FieldLink {

  public defaultSortField: string;
  public defaultSortOrder: number = -1;
  public pageSize: number = 25;

  public searchQuery: string = '';
  public filters: Filter[] = [];
  public columns: ColumnModel[] = [];
  public linkColumns: string[] = [];
  public notSortableColumns: string[] = [];
  public items: T[] = [];
  public loading: boolean = true;

  public pageRequestDto: PageRequestDto;

  public lastCreatedId: any;
  public blinkId: any;

  @ViewChild(Table, { static: true })
  public table: Table;

  private filterRequests: FilterRequestDto[] = [];

  private datePipe: DatePipe = new DatePipe("en-US");

  private dateFormat: string = "yyyy-MM-dd";

  private lazyReferenceTransformers: LazyReferenceFilterValueTransformer[] = [];
  private autoCompleteHandlers: AutocompleteHandler[] = [];

  protected constructor(public messageService: MessageService,
                        public fieldService: FieldService) {
  }

  public addLazyReferenceTransformers(transformer: LazyReferenceFilterValueTransformer): void {
    this.lazyReferenceTransformers.push(transformer);
  }

  public addAutoCompleteHandler(autocompleteHandler: AutocompleteHandler): void {
    this.autoCompleteHandlers.push(autocompleteHandler);
  }

  public getNextPage(pageRequest: SimplePageRequestDto, showLoader: boolean) {
    this.loading = showLoader;
    this.getNextPageAsObservable(pageRequest)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (pageDto: PageDto<T>) => {
          this.setPage(pageDto);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public abstract getNextPageAsObservable(pageRequest: SimplePageRequestDto): Observable<PageDto<T>>;

  public downloadReport(observable: Observable<Blob>, fileName: string): void {
    this.loading = true;
    observable
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (blob: Blob) => {
          saveAs(blob, fileName);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  public onLazyLoad(event: LazyLoadEvent) {
    const page: number = Math.round(event.first / event.rows);
    if (this.table.sortMode == 'multiple' && event.multiSortMeta && event.multiSortMeta.length > 0) {
      this.performPageRequest(page, event.rows, event.multiSortMeta, true);
    } else if (event.sortField) {
      const sortMeta = [
        {
          field: event.sortField,
          order: event.sortOrder
        }
      ];
      this.performPageRequest(page, event.rows, sortMeta, true);
    } else {
      this.performPageRequest(page, event.rows, [], true);
    }
  }

  public onSearch(searchQuery: string) {
    this.searchQuery = searchQuery;
    this.reloadPageWithLoader();
  }

  public onApplyFilter() {
    this.rebuildFilterRequests();
    this.reloadPageWithLoader();
  }

  public setPage(pageDto: PageDto<T>) {
    this.blinkId = this.lastCreatedId;
    this.lastCreatedId = null;
    this.items = pageDto.content;
    this.table.totalRecords = pageDto.totalCount;
    if (pageDto.page == 0) {
      this.table.first = 0;
    }
  }

  public getColumnValue(column: string, item: T) {
    return this.fieldService.getFieldValue(column, item);
  }

  public isLink(column: string): boolean {
    return this.linkColumns.includes(column);
  }

  public notSortable(column: string): boolean {
    return this.notSortableColumns.includes(column);
  }

  public reloadPageWithLoader() {
    this.reloadPage(true);
  }

  public reloadPage(showLoader: boolean) {
    if (this.table.sortMode == 'multiple' && this.table.multiSortMeta && this.table.multiSortMeta.length > 0) {
      this.performPageRequest(0, this.pageSize, this.table.multiSortMeta, showLoader);
    } else if (this.table.sortField) {
      const sortMeta = [
        {
          field: this.table.sortField,
          order: this.table.sortOrder
        }
      ];
      this.performPageRequest(0, this.pageSize, sortMeta, showLoader);
    } else {
      this.performPageRequest(0, this.pageSize, [], showLoader);
    }
  }

  public performPageRequest(page: number, size: number, sortMeta: SortMeta[], showLoader: boolean) {
    this.pageRequestDto = {
      page: page,
      size: size,
      sortFields: sortMeta.map((sort: SortMeta) => { return { sortField: this.getSortField(sort.field), ascending: sort.order == 1}; }),
      searchQuery: this.searchQuery,
      filters: this.filterRequests
    };
    this.getNextPage(this.pageRequestDto, showLoader);
  }

  public isBlink(item: any): boolean {
    return this.blinkId && this.blinkId == item.id;
  }

  public onFilterFieldAutocomplete(autocompleteItemModel: AutocompleteItemModel): void {
    const autocompleteHandler = this.autoCompleteHandlers
      .filter((item: AutocompleteHandler) => item.getFilterField() == autocompleteItemModel.filterField)
      .pop();
    if (autocompleteHandler) {
      autocompleteHandler.onFilterFieldAutocomplete(this.filters, autocompleteItemModel);
    }
  }

  private getSortField(columnName: string): string {
    const column: ColumnModel = this.columns.find((column: ColumnModel) => columnName == column.name);
    if (column) {
      return column.sortBy;
    }
    return null;
  }

  private rebuildFilterRequests(): void {
    this.filterRequests = this.filters.filter((filter: Filter) => this.hasValue(filter)).map((filter: Filter) => {
      return { name: filter.name, values: this.transformFilterValues(filter), matchMode: filter.matchMode };
    });
  }

  private hasValue(filter: Filter): boolean {
    return filter.multiple ? filter.currentValues && filter.currentValues.length > 0 : filter.currentValue;
  }

  private transformFilterValues(filter: Filter): string[] {
    const values: any[] = filter.multiple ? filter.currentValues.filter(item => item) : [filter.currentValue];
    return this.transformValues(filter, values);
  }

  private transformValues(filter: Filter, values: any[]): string[] {
    switch (filter.filterFieldType) {
      case "DATE":
        return values.map((item) => this.datePipe.transform(item, this.dateFormat));
      case "REFERENCE":
        return values.map((item) => item.value);
      case "LAZY_REFERENCE":
        return this.transformLazyReferenceValue(filter, values);
      default:
        return values;
    }
  }

  private transformLazyReferenceValue(filter: Filter, values: any[]): string[] {
    const transformer = this.lazyReferenceTransformers
      .filter((item: LazyReferenceFilterValueTransformer) => item.getFilterField() == filter.name)
      .pop();
    if (transformer) {
      return transformer.transformValues(filter, values);
    }
    return values.map((item) => item.value);
  }
}

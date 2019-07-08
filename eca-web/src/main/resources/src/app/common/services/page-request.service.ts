import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  PageRequestDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Injectable()
export class PageRequestService {

  public convertToHttpRequestParams(pageRequest: PageRequestDto): HttpParams {
    let params = new HttpParams().set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString())
      .set('sortField', pageRequest.sortField)
      .set('ascending', pageRequest.ascending.toString())
      .set('searchQuery', pageRequest.searchQuery);
    pageRequest.filters.map((filter, index) => {
      params = params.set(`filters['${index}'].name`, filter.name);
      filter.values.map((value, valueIndex) => {
        params = params.set(`filters['${index}'].values['${valueIndex}']`, value);
      });
      params = params.set(`filters['${index}'].filterFieldType`, filter.filterFieldType);
      params = params.set(`filters['${index}'].matchMode`, filter.matchMode);
    });
    return params;
  }
}

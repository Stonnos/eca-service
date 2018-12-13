import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  FilterDictionaryValueDto,
  FilterFieldDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { CookieService } from "ngx-cookie-service";
import { Filter } from "../filter.model";
import { SelectItem } from "primeng/api";

@Injectable()
export class FilterService {

  private serviceUrl = ConfigService.appConfig.apiUrl;

  public constructor(private http: HttpClient, private cookieService: CookieService) {
  }

  public getExperimentFilterFields(): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    return this.http.get<FilterFieldDto[]>(this.serviceUrl + '/filter-templates/experiment', { headers: headers });
  }

  public getEvaluationLogFilterFields(): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    return this.http.get<FilterFieldDto[]>(this.serviceUrl + '/filter-templates/evaluation', { headers: headers });
  }

  public getClassifierOptionsRequestFilterFields(): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    return this.http.get<FilterFieldDto[]>(this.serviceUrl + '/filter-templates/classifier-options-request', { headers: headers });
  }

  public mapToFilters(filterFields: FilterFieldDto[]): Filter[] {
    return filterFields.map((filter: FilterFieldDto) => {
      let values: SelectItem[] = [];
      if (filter.filterType == 'REFERENCE' && !!filter.dictionary && !!filter.dictionary.values && filter.dictionary.values.length > 0) {
        values = filter.dictionary.values.map((filterValue: FilterDictionaryValueDto) => {
          return { label: filterValue.label, value: filterValue.value };
        });
      }
      return new Filter(filter.name, filter.description, filter.filterType, filter.matchMode, null, values);
    });
  }
}

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  FilterDictionaryDto,
  FilterDictionaryValueDto,
  FilterFieldDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { Filter } from "../filter.model";
import { SelectItem } from "primeng/api";
import { AuthenticationKeys } from "../../auth/model/auth.keys";

@Injectable()
export class FilterService {

  private serviceUrl = ConfigService.appConfig.apiUrl + '/filter-templates';

  public constructor(private http: HttpClient) {
  }

  public getExperimentFilterFields(): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<FilterFieldDto[]>(this.serviceUrl + '/experiment', { headers: headers });
  }

  public getEvaluationLogFilterFields(): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<FilterFieldDto[]>(this.serviceUrl + '/evaluation', { headers: headers });
  }

  public getClassifierOptionsRequestFilterFields(): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<FilterFieldDto[]>(this.serviceUrl + '/classifier-options-request', { headers: headers });
  }

  public getExperimentTypeDictionary(): Observable<FilterDictionaryDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<FilterDictionaryDto>(this.serviceUrl + '/experiment-types', { headers: headers });
  }

  public getEvaluationMethodDictionary(): Observable<FilterDictionaryDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<FilterDictionaryDto>(this.serviceUrl + '/evaluation-methods', { headers: headers });
  }

  public mapToFilters(filterFields: FilterFieldDto[]): Filter[] {
    return filterFields.map((filter: FilterFieldDto) => {
      let values: SelectItem[] = [];
      if (filter.filterFieldType == 'REFERENCE' && !!filter.dictionary && !!filter.dictionary.values && filter.dictionary.values.length > 0) {
        values = filter.dictionary.values.map((filterValue: FilterDictionaryValueDto) => {
          return { label: filterValue.label, value: filterValue.value };
        });
      }
      return new Filter(filter.fieldName, filter.description, filter.filterFieldType, filter.matchMode, null, values);
    });
  }
}

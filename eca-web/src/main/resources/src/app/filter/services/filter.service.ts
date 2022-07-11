import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  FilterDictionaryDto,
  FilterDictionaryValueDto,
  FilterFieldDto,
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { Filter } from "../model/filter.model";
import { SelectItem } from "primeng/api";
import { FilterFieldType } from "../../common/model/filter-field-type.enum";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class FilterService {

  private ecaServerUrl = environment.serverUrl + '/filter-templates';
  private auditLogsUrl = environment.auditLogUrl + '/audit-log/filter-templates';

  public constructor(private http: HttpClient) {
  }

  public getExperimentFilterFields(): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<FilterFieldDto[]>(this.ecaServerUrl + '/experiment', { headers: headers });
  }

  public getEvaluationLogFilterFields(): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<FilterFieldDto[]>(this.ecaServerUrl + '/evaluation', { headers: headers });
  }

  public getClassifierOptionsRequestFilterFields(): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<FilterFieldDto[]>(this.ecaServerUrl + '/classifier-options-request', { headers: headers });
  }

  public getExperimentTypeDictionary(): Observable<FilterDictionaryDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<FilterDictionaryDto>(this.ecaServerUrl + '/experiment-types', { headers: headers });
  }

  public getEvaluationMethodDictionary(): Observable<FilterDictionaryDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<FilterDictionaryDto>(this.ecaServerUrl + '/evaluation-methods', { headers: headers });
  }

  public getAuditLogFilterFields(): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<FilterFieldDto[]>(this.auditLogsUrl + '/fields', { headers: headers });
  }

  public getClassifiersConfigurationHistoryFilterFields(): Observable<FilterFieldDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<FilterFieldDto[]>(this.ecaServerUrl + '/classifiers-configuration-history', { headers: headers });
  }

  public mapToFilters(filterFields: FilterFieldDto[]): Filter[] {
    return filterFields.map((filter: FilterFieldDto) => {
      let values: SelectItem[] = [];
      if (filter.filterFieldType == FilterFieldType.REFERENCE && filter.dictionary && filter.dictionary.values && filter.dictionary.values.length > 0) {
        values = filter.dictionary.values.map((filterValue: FilterDictionaryValueDto) => {
          return { label: filterValue.label, value: filterValue.value };
        });
      }
      return new Filter(filter.fieldName, filter.description, filter.filterFieldType, filter.matchMode, filter.multiple, values);
    });
  }
}

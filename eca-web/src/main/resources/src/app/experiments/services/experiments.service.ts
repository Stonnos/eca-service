import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ExperimentDto, ExperimentStatisticsDto, ExperimentTypeDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";

@Injectable()
export class ExperimentsService {

  private serviceUrl = "http://localhost:8085/eca-server";

  public constructor(private http: HttpClient) {
  }

  public getExperiments(pageRequest: PageRequestDto): Observable<PageDto<ExperimentDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
    });
    let params = new HttpParams().set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString())
      .set('sortField', pageRequest.sortField)
      .set('ascending', pageRequest.ascending.toString());
    pageRequest.filters.map((filter, index) => {
      params = params.set(`filters['${index}'].name`, filter.name);
      params = params.set(`filters['${index}'].value`, filter.value);
      params = params.set(`filters['${index}'].filterType`, filter.filterType);
      params = params.set(`filters['${index}'].matchMode`, filter.matchMode);
    });
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<ExperimentDto>>(this.serviceUrl + '/experiments', options);
  }

  public getExperimentTypes(): Observable<ExperimentTypeDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
    });
    return this.http.get<ExperimentTypeDto[]>(this.serviceUrl + '/experiment-types', { headers: headers });
  }

  public getExperimentsStatistics(): Observable<ExperimentStatisticsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
    });
    return this.http.get<ExperimentStatisticsDto>(this.serviceUrl + '/experiments-statistics', { headers: headers });
  }

  public getExperimentResultsFile(uuid: string): Observable<Blob> {
    return this.http.get<Blob>(this.serviceUrl + '/experiment/download/' + uuid, { responseType: 'blob' as 'json' });
  }
}

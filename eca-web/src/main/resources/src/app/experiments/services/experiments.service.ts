import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ExperimentDto, PageDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";

@Injectable()
export class ExperimentsService {

  private serviceUrl = "http://localhost:8085/eca-server/experiments";

  public constructor(private http: HttpClient) {
  }

  public getExperiments(page: number, size: number, sortField: string, ascending: Boolean): Observable<PageDto<ExperimentDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
    });
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString())
      .set('sortField', sortField).set('ascending', ascending.toString());
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<ExperimentDto>>(this.serviceUrl, options);
  }
}

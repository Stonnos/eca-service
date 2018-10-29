import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ExperimentDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";

@Injectable()
export class ExperimentsService {

  private serviceUrl = "http://localhost:8085/eca-server/experiments";

  public constructor(private http: HttpClient) {
  }

  public getExperiments(): Observable<ExperimentDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
    });
    return this.http.get<ExperimentDto[]>(this.serviceUrl, { headers: headers });
  }
}

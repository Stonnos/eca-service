import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EvaluationLogDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";

@Injectable()
export class ClassifiersService {

  private serviceUrl = "http://localhost:8085/eca-server/evaluations";

  public constructor(private http: HttpClient) {
  }

  public getEvaluations(): Observable<EvaluationLogDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
    });
    return this.http.get<EvaluationLogDto[]>(this.serviceUrl, { headers: headers });
  }
}

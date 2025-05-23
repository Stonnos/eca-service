import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { Observable } from "rxjs/internal/Observable";
import { catchError, filter, finalize, switchMap, take } from "rxjs/internal/operators";
import { EMPTY } from "rxjs/internal/observable/empty";
import { throwError } from "rxjs/internal/observable/throwError";
import { LogoutService } from "../services/logout.service";
import { BehaviorSubject } from "rxjs/internal/BehaviorSubject";
import { AuthService } from "../services/auth.service";
import { Router } from "@angular/router";
import { EventService } from "../../common/event/event.service";
import { EventType } from "../../common/event/event.type";
import { Logger } from "../../common/util/logging";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  private static readonly TOKEN_URL = '/oauth2/token';

  private refreshTokenInProgress = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(private injector: Injector,
              private logoutService: LogoutService,
              private eventService: EventService,
              private router: Router) {
  }

  public intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(catchError(error => {
      if (error instanceof HttpErrorResponse) {
        // Try to refresh access token
        if (error.status === 401) {
          if (request.url.includes(AuthInterceptor.TOKEN_URL)) {
            Logger.debug(`401 Unauthorized for ${AuthInterceptor.TOKEN_URL} url`);
            this.logoutService.logout();
            return EMPTY;
          }
          if (this.refreshTokenInProgress) {
            return this.refreshTokenSubject.pipe(
              filter(result => result),
              take(1),
              switchMap(() => next.handle(request))
            );
          } else {
            Logger.debug('Starting to refresh token');
            this.refreshTokenInProgress = true;
            this.refreshTokenSubject.next(null);
            this.eventService.publishEvent(EventType.TOKEN_EXPIRED);
            return this.injector.get(AuthService)
              .refreshToken().pipe(
                switchMap(token => {
                  this.refreshTokenSubject.next(token);
                  Logger.debug('Token has been refreshed');
                  this.eventService.publishEvent(EventType.TOKEN_REFRESHED);
                  return next.handle(request);
                }),
                catchError(error => {
                  Logger.debug(`Refresh token expired. Handle refresh token error response with http status code ${error.status}`);
                  this.logoutService.logout();
                  return EMPTY;
                }),
                finalize(() => {
                  this.refreshTokenInProgress = false;
                })
              );
          }
        } else if (error.status === 403 && !request.url.includes(AuthInterceptor.TOKEN_URL)) {
          this.router.navigate(['/access-denied']);
          return EMPTY;
        }
      }
      return throwError(error);
    }));
  }
}

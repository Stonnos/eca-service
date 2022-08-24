import { Injectable } from "@angular/core";
import { Subject } from "rxjs/internal/Subject";
import { Observable } from "rxjs/internal/Observable";
import { EventType } from "./event.type";

@Injectable()
export class EventService {

  private eventSubject: Subject<EventType> = new Subject<EventType>();

  public publishEvent(eventType: EventType): void {
    this.eventSubject.next(eventType);
  }

  public eventSubscribe(): Observable<EventType> {
    return this.eventSubject.asObservable();
  }
}

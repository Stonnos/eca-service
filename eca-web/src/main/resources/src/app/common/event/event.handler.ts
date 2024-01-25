import { Injectable } from "@angular/core";
import { EventService } from "./event.service";
import { PushService } from "../push/push.service";
import { MessageService } from "primeng/api";
import { EventType } from "./event.type";
import { Logger } from "../util/logging";

@Injectable()
export class EventHandler {

  public constructor(private eventService: EventService,
                     private pushService: PushService,
                     private messageService: MessageService) {
  }

  public eventSubscribe() {
    return this.eventService.eventSubscribe()
      .subscribe({
        next: (eventType: EventType) => {
          Logger.debug(`Handle event ${eventType}`);
          switch (eventType) {
            case EventType.INIT_PUSH:
              this.pushService.init();
              break;
            case EventType.TOKEN_REFRESHED:
              this.handleTokenRefreshedEvent();
              break;
            case EventType.TOKEN_EXPIRED:
            case EventType.LOGOUT:
              this.pushService.close();
              break;
            default:
              this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: `Can't handle event ${eventType}` });
          }
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private handleTokenRefreshedEvent(): void {
    this.pushService.init();
  }
}

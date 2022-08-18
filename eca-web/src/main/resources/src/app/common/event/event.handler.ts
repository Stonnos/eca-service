import { Injectable } from "@angular/core";
import { EventService } from "./event.service";
import { PushService } from "../push/push.service";
import { MessageService } from "primeng/api";
import { EventType } from "./event.type";

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
          switch (eventType) {
            case EventType.TOKEN_REFRESHED:
              this.pushService.init();
              break;
            case EventType.TOKEN_EXPIRED:
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
}

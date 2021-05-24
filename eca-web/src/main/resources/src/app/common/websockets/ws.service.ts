import { StompService } from "@stomp/ng2-stompjs";
import { AuthenticationKeys } from "../../auth/model/auth.keys";

export class WsService {

  private stompService: StompService;

  public constructor() {
    this.stompService = new StompService({
      url: 'ws://localhost:8085/socket?access_token=' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN),
      headers: {
      },
      heartbeat_in: 0,
      heartbeat_out: 20000,
      reconnect_delay: 5000,
      debug: true
    });
  }

  public subscribe(queueName: string) {
    return this.stompService.subscribe(queueName);
  }

  public close(): void {
    this.stompService.disconnect();
    console.log('Close connection');
  }
}

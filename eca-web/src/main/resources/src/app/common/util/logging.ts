import { environment } from "../../../environments/environment";

export class Logger {

  public static debug(message: string): void {
    if (environment.debug) {
      console.log(message);
    }
  }
}

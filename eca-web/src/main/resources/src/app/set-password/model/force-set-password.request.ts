export class ForceSetPasswordRequest {
  token: string;
  confirmationCode: string;
  password: string;

  public constructor(token: string, confirmationCode: string, password: string) {
    this.token = token;
    this.confirmationCode = confirmationCode;
    this.password = password;
  }
}

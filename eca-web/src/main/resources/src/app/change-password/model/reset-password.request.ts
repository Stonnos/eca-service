export class ResetPasswordRequest {
  token: string;
  password: string;

  public constructor(token: string, password: string) {
    this.token = token;
    this.password = password;
  }
}

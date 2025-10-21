export class CreatePersonalAccessTokenDto {
  name: string;
  tokenType: string;
  expirationMonth: number;

  public constructor(name: string, tokenType: string, expirationMonth: number) {
    this.name = name;
    this.tokenType = tokenType;
    this.expirationMonth = expirationMonth;
  }
}

export class CreatePersonalAccessTokenModel {
  name: string;
  tokenType: any;
  expirationMonth: any;
}

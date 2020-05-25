import { Injectable } from "@angular/core";
import { AuthenticationKeys } from "../model/auth.keys";
import { UserDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Injectable()
export class UserStorage {

  public saveUser(user: UserDto) {
    localStorage.setItem(AuthenticationKeys.USER_NAME, JSON.stringify(user));
  }

  public getUser(): UserDto {
    return JSON.parse(localStorage.getItem(AuthenticationKeys.USER_NAME));
  }
}

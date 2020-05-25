import { Injectable } from "@angular/core";
import { AuthenticationKeys } from "../model/auth.keys";
import { UserDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Injectable()
export class UserStorage {

  public saveUser(user: UserDto) {
    localStorage.setItem(AuthenticationKeys.USER, JSON.stringify(user));
  }

  public getUser(): UserDto {
    const jsonUser: string = localStorage.getItem(AuthenticationKeys.USER);
    return jsonUser && JSON.parse(jsonUser);
  }
}

export class UpdateUserInfoModel {
  firstName: string;
  lastName: string;
  middleName: string;

  constructor(firstName: string, lastName: string, middleName: string) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.middleName = middleName;
  }
}

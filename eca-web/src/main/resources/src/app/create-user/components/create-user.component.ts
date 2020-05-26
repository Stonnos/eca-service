import { Component, OnInit } from '@angular/core';
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { CreateUserModel } from "../model/create-user.model";

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss']
})
export class CreateUserComponent extends BaseCreateDialogComponent<CreateUserModel> implements OnInit {

  public loginRegex: string = '[a-z0-9]*';
  public emailRegex: string = '[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})';

  public ngOnInit(): void {
  }

}

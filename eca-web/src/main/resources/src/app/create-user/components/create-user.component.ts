import { Component, OnInit } from '@angular/core';
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { CreateUserModel } from "../model/create-user.model";

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss']
})
export class CreateUserComponent extends BaseCreateDialogComponent<CreateUserModel> implements OnInit {

  public ngOnInit(): void {
  }

}

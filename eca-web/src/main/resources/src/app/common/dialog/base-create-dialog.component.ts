import { EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BaseDialog } from "./base-dialog";
import { BaseForm } from "../form/base-form";

export abstract class BaseCreateDialogComponent<T> implements BaseDialog, BaseForm {

  @Input()
  public item: T;

  @Input()
  public visible: boolean = false;

  public submitted: boolean = false;

  @Output()
  public itemEvent: EventEmitter<T> = new EventEmitter<T>();

  @Output()
  public visibilityChange: EventEmitter<boolean> = new EventEmitter();

  @ViewChild(NgForm)
  private form: NgForm;

  public hide() {
    this.clear();
    this.visibilityChange.emit(false);
  }

  public clear(): void {
    this.form.reset();
    this.submitted = false;
  }

  public submit() {
    this.submitted = true;
    if (this.isValid()) {
      this.itemEvent.emit(this.item);
      this.hide();
    }
  }

  public isValid(): boolean {
    return this.form.valid;
  }
}

import { Directive, Input } from '@angular/core';
import { NG_VALIDATORS, Validator, FormControl } from '@angular/forms';

@Directive({
  selector: '[maxNumber][formControlName],[maxNumber][formControl],[maxNumber][ngModel]',
  providers: [{provide: NG_VALIDATORS, useExisting: MaxNumberValidator, multi: true}]
})
export class MaxNumberValidator implements Validator {

  @Input()
  public maxNumber: number;

  public validate(control: FormControl): {[key: string]: any} {
    let value = control.value;
    return this.maxNumber != null && value != null && value > this.maxNumber ? {"maxNumber": true} : null;
  }
}

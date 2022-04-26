import { Directive, Input } from '@angular/core';
import { NG_VALIDATORS, Validator, FormControl } from '@angular/forms';

@Directive({
  selector: '[maxNumber][formControlName],[maxNumber][formControl],[maxNumber][ngModel]',
  providers: [{provide: NG_VALIDATORS, useExisting: MaxNumberValidator, multi: true}]
})
export class MaxNumberValidator implements Validator {

  @Input()
  public maxNumber: number;
  @Input()
  public maxInclusive: boolean;

  public validate(control: FormControl): {[key: string]: any} {
    let value = control.value;
    if (this.maxNumber != null && value != null) {
      if (this.maxInclusive) {
        return value > this.maxNumber ? {"maxNumber": true} : null;
      } else {
        return value >= this.maxNumber ? {"maxNumber": true} : null;
      }
    }
    return null;
  }
}

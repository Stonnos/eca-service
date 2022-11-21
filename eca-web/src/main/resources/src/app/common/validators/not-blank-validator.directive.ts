import { Directive } from '@angular/core';
import { NG_VALIDATORS, Validator, FormControl } from '@angular/forms';

@Directive({
  selector: '[notBlank][formControlName],[notBlank][formControl],[notBlank][ngModel]',
  providers: [{provide: NG_VALIDATORS, useExisting: NotBlankValidator, multi: true}]
})
export class NotBlankValidator implements Validator {

  public validate(control: FormControl): {[key: string]: any} {
    let value = control.value;
    if (value != null) {
      let trimValue = value.toString().trim();
      if (trimValue.length == 0) {
        return {"notBlank": true}
      }
    }
    return null;
  }
}

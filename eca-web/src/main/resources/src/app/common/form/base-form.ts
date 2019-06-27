export interface BaseForm {
  submitted: boolean;

  clear(): void;

  submit(): void;

  isValid(): boolean;
}

import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class GlobalStateService {

  private values = new Map<string, string>();

  public setValue(key: string, value: string): void {
    this.values.set(key, value);
  }

  public getValue(key: string): string {
    return this.values.get(key);
  }

  public remove(key: string): void {
    this.values.delete(key);
  }
}

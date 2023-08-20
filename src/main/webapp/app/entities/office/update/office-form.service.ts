import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IOffice, NewOffice } from '../office.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOffice for edit and NewOfficeFormGroupInput for create.
 */
type OfficeFormGroupInput = IOffice | PartialWithRequiredKeyOf<NewOffice>;

type OfficeFormDefaults = Pick<NewOffice, 'id'>;

type OfficeFormGroupContent = {
  id: FormControl<IOffice['id'] | NewOffice['id']>;
  state: FormControl<IOffice['state']>;
  municipality: FormControl<IOffice['municipality']>;
  federal: FormControl<IOffice['federal']>;
};

export type OfficeFormGroup = FormGroup<OfficeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OfficeFormService {
  createOfficeFormGroup(office: OfficeFormGroupInput = { id: null }): OfficeFormGroup {
    const officeRawValue = {
      ...this.getFormDefaults(),
      ...office,
    };
    return new FormGroup<OfficeFormGroupContent>({
      id: new FormControl(
        { value: officeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      state: new FormControl(officeRawValue.state),
      municipality: new FormControl(officeRawValue.municipality, {
        validators: [Validators.maxLength(128)],
      }),
      federal: new FormControl(officeRawValue.federal),
    });
  }

  getOffice(form: OfficeFormGroup): IOffice | NewOffice {
    return form.getRawValue() as IOffice | NewOffice;
  }

  resetForm(form: OfficeFormGroup, office: OfficeFormGroupInput): void {
    const officeRawValue = { ...this.getFormDefaults(), ...office };
    form.reset(
      {
        ...officeRawValue,
        id: { value: officeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OfficeFormDefaults {
    return {
      id: null,
    };
  }
}

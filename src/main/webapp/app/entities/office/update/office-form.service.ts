import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
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

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOffice | NewOffice> = Omit<T, 'electionDate'> & {
  electionDate?: string | null;
};

type OfficeFormRawValue = FormValueOf<IOffice>;

type NewOfficeFormRawValue = FormValueOf<NewOffice>;

type OfficeFormDefaults = Pick<NewOffice, 'id' | 'electionDate'>;

type OfficeFormGroupContent = {
  id: FormControl<OfficeFormRawValue['id'] | NewOffice['id']>;
  name: FormControl<OfficeFormRawValue['name']>;
  municipality: FormControl<OfficeFormRawValue['municipality']>;
  state: FormControl<OfficeFormRawValue['state']>;
  electionDate: FormControl<OfficeFormRawValue['electionDate']>;
};

export type OfficeFormGroup = FormGroup<OfficeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OfficeFormService {
  createOfficeFormGroup(office: OfficeFormGroupInput = { id: null }): OfficeFormGroup {
    const officeRawValue = this.convertOfficeToOfficeRawValue({
      ...this.getFormDefaults(),
      ...office,
    });
    return new FormGroup<OfficeFormGroupContent>({
      id: new FormControl(
        { value: officeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(officeRawValue.name, {
        validators: [Validators.maxLength(128)],
      }),
      municipality: new FormControl(officeRawValue.municipality, {
        validators: [Validators.maxLength(128)],
      }),
      state: new FormControl(officeRawValue.state),
      electionDate: new FormControl(officeRawValue.electionDate),
    });
  }

  getOffice(form: OfficeFormGroup): IOffice | NewOffice {
    return this.convertOfficeRawValueToOffice(form.getRawValue() as OfficeFormRawValue | NewOfficeFormRawValue);
  }

  resetForm(form: OfficeFormGroup, office: OfficeFormGroupInput): void {
    const officeRawValue = this.convertOfficeToOfficeRawValue({ ...this.getFormDefaults(), ...office });
    form.reset(
      {
        ...officeRawValue,
        id: { value: officeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OfficeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      electionDate: currentTime,
    };
  }

  private convertOfficeRawValueToOffice(rawOffice: OfficeFormRawValue | NewOfficeFormRawValue): IOffice | NewOffice {
    return {
      ...rawOffice,
      electionDate: dayjs(rawOffice.electionDate, DATE_TIME_FORMAT),
    };
  }

  private convertOfficeToOfficeRawValue(
    office: IOffice | (Partial<NewOffice> & OfficeFormDefaults)
  ): OfficeFormRawValue | PartialWithRequiredKeyOf<NewOfficeFormRawValue> {
    return {
      ...office,
      electionDate: office.electionDate ? office.electionDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}

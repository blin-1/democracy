import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICandidate, NewCandidate } from '../candidate.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICandidate for edit and NewCandidateFormGroupInput for create.
 */
type CandidateFormGroupInput = ICandidate | PartialWithRequiredKeyOf<NewCandidate>;

type CandidateFormDefaults = Pick<NewCandidate, 'id'>;

type CandidateFormGroupContent = {
  id: FormControl<ICandidate['id'] | NewCandidate['id']>;
  firstName: FormControl<ICandidate['firstName']>;
  lastName: FormControl<ICandidate['lastName']>;
  email: FormControl<ICandidate['email']>;
  party: FormControl<ICandidate['party']>;
  pic: FormControl<ICandidate['pic']>;
  picContentType: FormControl<ICandidate['picContentType']>;
  bio: FormControl<ICandidate['bio']>;
  office: FormControl<ICandidate['office']>;
};

export type CandidateFormGroup = FormGroup<CandidateFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CandidateFormService {
  createCandidateFormGroup(candidate: CandidateFormGroupInput = { id: null }): CandidateFormGroup {
    const candidateRawValue = {
      ...this.getFormDefaults(),
      ...candidate,
    };
    return new FormGroup<CandidateFormGroupContent>({
      id: new FormControl(
        { value: candidateRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      firstName: new FormControl(candidateRawValue.firstName, {
        validators: [Validators.required, Validators.maxLength(128)],
      }),
      lastName: new FormControl(candidateRawValue.lastName, {
        validators: [Validators.required, Validators.maxLength(128)],
      }),
      email: new FormControl(candidateRawValue.email, {
        validators: [Validators.required],
      }),
      party: new FormControl(candidateRawValue.party, {
        validators: [Validators.required],
      }),
      pic: new FormControl(candidateRawValue.pic, {
        validators: [Validators.required],
      }),
      picContentType: new FormControl(candidateRawValue.picContentType),
      bio: new FormControl(candidateRawValue.bio, {
        validators: [Validators.required],
      }),
      office: new FormControl(candidateRawValue.office),
    });
  }

  getCandidate(form: CandidateFormGroup): ICandidate | NewCandidate {
    return form.getRawValue() as ICandidate | NewCandidate;
  }

  resetForm(form: CandidateFormGroup, candidate: CandidateFormGroupInput): void {
    const candidateRawValue = { ...this.getFormDefaults(), ...candidate };
    form.reset(
      {
        ...candidateRawValue,
        id: { value: candidateRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CandidateFormDefaults {
    return {
      id: null,
    };
  }
}

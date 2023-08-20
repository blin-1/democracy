import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IIssue, NewIssue } from '../issue.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IIssue for edit and NewIssueFormGroupInput for create.
 */
type IssueFormGroupInput = IIssue | PartialWithRequiredKeyOf<NewIssue>;

type IssueFormDefaults = Pick<NewIssue, 'id'>;

type IssueFormGroupContent = {
  id: FormControl<IIssue['id'] | NewIssue['id']>;
  name: FormControl<IIssue['name']>;
  description: FormControl<IIssue['description']>;
  candidate: FormControl<IIssue['candidate']>;
};

export type IssueFormGroup = FormGroup<IssueFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class IssueFormService {
  createIssueFormGroup(issue: IssueFormGroupInput = { id: null }): IssueFormGroup {
    const issueRawValue = {
      ...this.getFormDefaults(),
      ...issue,
    };
    return new FormGroup<IssueFormGroupContent>({
      id: new FormControl(
        { value: issueRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(issueRawValue.name, {
        validators: [Validators.required, Validators.maxLength(128)],
      }),
      description: new FormControl(issueRawValue.description, {
        validators: [Validators.required],
      }),
      candidate: new FormControl(issueRawValue.candidate),
    });
  }

  getIssue(form: IssueFormGroup): IIssue | NewIssue {
    return form.getRawValue() as IIssue | NewIssue;
  }

  resetForm(form: IssueFormGroup, issue: IssueFormGroupInput): void {
    const issueRawValue = { ...this.getFormDefaults(), ...issue };
    form.reset(
      {
        ...issueRawValue,
        id: { value: issueRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): IssueFormDefaults {
    return {
      id: null,
    };
  }
}

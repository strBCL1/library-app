import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPublisher, NewPublisher } from '../publisher.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPublisher for edit and NewPublisherFormGroupInput for create.
 */
type PublisherFormGroupInput = IPublisher | PartialWithRequiredKeyOf<NewPublisher>;

type PublisherFormDefaults = Pick<NewPublisher, 'id'>;

type PublisherFormGroupContent = {
  id: FormControl<IPublisher['id'] | NewPublisher['id']>;
  firstName: FormControl<IPublisher['firstName']>;
  lastName: FormControl<IPublisher['lastName']>;
  address: FormControl<IPublisher['address']>;
  phoneNumber: FormControl<IPublisher['phoneNumber']>;
};

export type PublisherFormGroup = FormGroup<PublisherFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PublisherFormService {
  createPublisherFormGroup(publisher: PublisherFormGroupInput = { id: null }): PublisherFormGroup {
    const publisherRawValue = {
      ...this.getFormDefaults(),
      ...publisher,
    };
    return new FormGroup<PublisherFormGroupContent>({
      id: new FormControl(
        { value: publisherRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      firstName: new FormControl(publisherRawValue.firstName, {
        validators: [Validators.maxLength(150)],
      }),
      lastName: new FormControl(publisherRawValue.lastName, {
        validators: [Validators.maxLength(150)],
      }),
      address: new FormControl(publisherRawValue.address, {
        validators: [Validators.maxLength(200)],
      }),
      phoneNumber: new FormControl(publisherRawValue.phoneNumber, {
        validators: [Validators.minLength(12), Validators.maxLength(12)],
      }),
    });
  }

  getPublisher(form: PublisherFormGroup): IPublisher | NewPublisher {
    return form.getRawValue() as IPublisher | NewPublisher;
  }

  resetForm(form: PublisherFormGroup, publisher: PublisherFormGroupInput): void {
    const publisherRawValue = { ...this.getFormDefaults(), ...publisher };
    form.reset(
      {
        ...publisherRawValue,
        id: { value: publisherRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PublisherFormDefaults {
    return {
      id: null,
    };
  }
}

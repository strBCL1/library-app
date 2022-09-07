export interface IPublisher {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  address?: string | null;
  phoneNumber?: string | null;
}

export type NewPublisher = Omit<IPublisher, 'id'> & { id: null };

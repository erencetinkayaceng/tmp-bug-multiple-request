export interface IFoo {
  id: number;
  name?: string | null;
}

export type NewFoo = Omit<IFoo, 'id'> & { id: null };

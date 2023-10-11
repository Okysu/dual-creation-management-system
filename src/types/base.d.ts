declare type User = {
  id: number;
  uid: number;
  password: string | null;
  name: string;
  mail: string;
  expire: null | string;
  disabled: number;
  menu?: string;
  role?: number;
};

declare type Menu = {
  label: string;
  key: string;
  icon: string;
  children?: Menu[];
};

declare type Log = {
  id: number;
  uid: number;
  ip: string;
  time: string;
  url: string;
  method: string;
  status: string;
};

declare type RedisRecord = {
  key: string;
  value: string;
  ttl: number;
};

declare type Pagination = {
  page: number;
  size: number;
};

declare type Role = {
  id: number;
  name: string;
  description: string;
  count?: number;
};

declare type Lab = {
  id: number;
  name: string;
  description: string;
  createdTime: string;
  delFlag: number;
  teachers?: User[];
  students?: User[];
  user?: User;
};

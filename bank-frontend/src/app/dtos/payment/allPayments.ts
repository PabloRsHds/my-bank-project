export interface AllPayments {
  userSend : string;
  userReceive : string;
  money : number;
  sendOrReceive : 'SEND' | 'RECEIVE';
  timeStamp : Date;
  fullName : string;
}

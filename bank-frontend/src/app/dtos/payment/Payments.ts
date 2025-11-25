export interface Payments {
  userSend : string;
  userReceive : string;
  money : number;
  sendOrReceive : 'SEND' | 'RECEIVE';
  timeStamp : Date;
}

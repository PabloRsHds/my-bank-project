export interface RequestPayment {
  money : number;
  key : string;
  pixOrCredit : 'PIX' | 'CREDIT';
}

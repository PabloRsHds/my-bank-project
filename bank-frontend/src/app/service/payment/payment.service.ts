import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Notifications } from '../../dtos/notification/Notifications';
import { Payments } from '../../dtos/payment/Payments';
import { RequestCreditPayment } from '../../dtos/payment/RequestCreditPayment';
import { RequestPayment } from '../../dtos/payment/RequestPayment';
import { ResponseUser } from '../../dtos/user/ResponseUser';
import { ResponseWallet } from '../../dtos/wallet/ResponseWallet';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  constructor(private http:HttpClient) { }

  /**
   * Obtém os dados completos do usuário pelo ID
   * @param userId - ID do usuário a ser buscado
   * @returns Observable com os dados do usuário
   * @endpoint GET http://localhost:8080/api/get-user-with-id
   */
  getUserWithId(userId: string): Observable<ResponseUser> {
    return this.http.get<ResponseUser>('http://localhost:8080/api/get-user-with-id', {params: { userId }
    });
  }

  /**
   * Obtém o saldo da carteira do usuário
   * @returns Observable<ResponseWallet> - Dados da carteira incluindo saldo
   * @endpoint GET http://localhost:8086/api/get-wallet
   */
  getWallet():Observable<ResponseWallet>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<ResponseWallet>('http://localhost:8086/api/get-wallet', { headers });
  }

  /**
   * Obtém o limite de crédito disponível do usuário
   * @returns Observable<number> - Valor do limite de crédito
   * @endpoint GET http://localhost:8083/api/get-limit-credit
   */
  getLimitOfCredit():Observable<number>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<number>('http://localhost:8083/api/get-limit-credit', { headers });
  }

  /**
   * Obtém o limite de crédito baseado no salário (30% do salário)
   * @returns Observable<number> - Valor do limite de crédito calculado
   * @endpoint GET http://localhost:8081/api/limit-credit
   */
  getLimitCreditSalary() {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<number>('http://localhost:8081/api/limit-credit', { headers });
  }

  /**
   * Realiza um pagamento via PIX ou Débito
   * @param request - Dados do pagamento: money, key, pixOrCredit
   * @returns Observable<string> - Confirmação do pagamento
   * @endpoint POST http://localhost:8086/api/payment
   */
  payment(request : RequestPayment):Observable<string>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.post<string>('http://localhost:8086/api/payment', request, { headers });
  }

  /**
   * Realiza um pagamento utilizando crédito
   * @param request - Dados do pagamento: money
   * @returns Observable<string> - Confirmação do pagamento
   * @endpoint POST http://localhost:8086/api/credit-payment
   */
  creditPayment(request : RequestCreditPayment):Observable<string>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.post<string>('http://localhost:8086/api/credit-payment', request, { headers });
  }

  /**
   * Obtém todos os pagamentos enviados pelo usuário
   * @returns Observable<Payments[]> - Lista de pagamentos enviados
   * @endpoint GET http://localhost:8086/api/get-send-payments
   */
  getAllSendPayments():Observable<Payments[]>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<Payments[]>('http://localhost:8086/api/get-send-payments', { headers });
  }

  /**
   * Obtém todos os pagamentos recebidos pelo usuário
   * @returns Observable<Payments[]> - Lista de pagamentos recebidos
   * @endpoint GET http://localhost:8086/api/get-receive-payments
   */
  getAllReceivePayments():Observable<Payments[]>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<Payments[]>('http://localhost:8086/api/get-receive-payments', { headers });
  }

  /**
   * Obtém o nome completo do usuário pelo ID
   * @param userId - ID do usuário
   * @returns Observable<string> - Nome completo do usuário
   * @endpoint GET http://localhost:8080/microservice/bank_user/full-name
   */
  getUserFullName(userId: string) : Observable<string> {
    return this.http.get('http://localhost:8080/api/full-name',
      {params : {userId}, responseType: 'text'});
  }

  /**
   * Verifica se o usuário atual possui privilégios de administrador
   * @returns Observable<boolean> - true se for administrador, false caso contrário
   * @endpoint GET http://localhost:8080/api/verify-if-user-admin
   */
  verifyIfUserIsAdm(): Observable<boolean>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<boolean>('http://localhost:8080/api/verify-if-user-admin', { headers });
  }

  /**
   * Verifica se o usuário está com a conta verificada
   * @returns Observable<boolean> - true se verificado, false caso contrário
   * @endpoint GET http://localhost:8080/api/check-user-verification
   */
  checkUserVerification():Observable<boolean>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<boolean>('http://localhost:8080/api/check-user-verification', { headers });
  }

  /**
   * Obtém todas as notificações do usuário
   * @returns Observable<Notifications[]> - Lista de notificações
   * @endpoint GET http://localhost:8084/api/notifications
   */
  allNotifications():Observable<Notifications[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<Notifications[]>('http://localhost:8084/api/notifications', { headers });
  }

  /**
   * Marca todas as notificações como visualizadas
   * @returns Observable<void>
   * @endpoint PUT http://localhost:8084/api/visualisation-notification
   */
  visualisationNotification(): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.put<void>('http://localhost:8084/api/visualisation-notification', { headers});
  }

  /**
   * Obtém a contagem de notificações não visualizadas
   * @returns Observable<number> - Número de notificações não visualizadas
   * @endpoint GET http://localhost:8084/api/count-notification
   */
  countNotifications() : Observable<number> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<number>('http://localhost:8084/api/count-notification', { headers });
  }

  /**
   * Oculta uma notificação específica
   * @param notificationId - ID da notificação a ser ocultada
   * @returns Observable<void>
   * @endpoint POST http://localhost:8084/api/occult-notification
   */
  ocultNotification(notificationId : number): Observable<void> {
    return this.http.post<void>('http://localhost:8084/api/occult-notification',
      {notificationId : notificationId});
  }
}

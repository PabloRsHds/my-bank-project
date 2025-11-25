import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResponseLoginHistory } from '../../dtos/login/ResponseLoginHistory';
import { Notifications } from '../../dtos/notification/Notifications';
import { RequestPasswordUpdate } from '../../dtos/user/RequestPasswordUpdate';
import { RequestPhoneUpdate } from '../../dtos/user/RequestPhoneUpdate';
import { ResponseUser } from '../../dtos/user/ResponseUser';

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {

  constructor(private http: HttpClient) { }

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
   * Atualiza a senha do usuário
   * @param request - Objeto contendo nova senha e senha antiga
   * @returns Observable<void>
   * @endpoint PUT http://localhost:8080/api/update-password
   */
  updatePassword(request:RequestPasswordUpdate):Observable<void>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.put<void>('http://localhost:8080/api/update-password', request, { headers });
  }

  /**
   * Atualiza o telefone do usuário
   * @param request - Objeto contendo novo número de telefone
   * @returns Observable<void>
   * @endpoint PUT http://localhost:8080/api/update-phone
   */
  updatePhone(request:RequestPhoneUpdate):Observable<void>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.put<void>('http://localhost:8080/api/update-phone', request, { headers });
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


  /**
   * Obtém o histórico de logins do usuário
   * @returns Observable<ResponseLoginHistory[]> - Lista de histórico de logins
   * @endpoint GET http://localhost:8085/api/login-history
   */
  loginHistory(): Observable<ResponseLoginHistory[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<ResponseLoginHistory[]>('http://localhost:8085/api/login-history', { headers });
  }

  /**
   * Exclui permanentemente a conta do usuário
   * @returns Observable<void>
   * @endpoint DELETE http://localhost:8080/api/delete
   */
  deleteAccount(): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.delete<void>('http://localhost:8080/api/delete', { headers });
  }
}

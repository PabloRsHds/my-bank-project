import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { ResponseUser } from '../../dtos/user/ResponseUser';
import { Notifications } from '../../dtos/notification/Notifications';
import { ResponseUserCard } from '../../dtos/card/ResponseUserCard';

@Injectable({
  providedIn: 'root'
})
export class AdmClientService {

  constructor(private http: HttpClient) { }

  /**
   * Obtém os dados completos do usuário pelo ID
   * @param userId - ID do usuário a ser buscado
   * @returns Observable com os dados do usuário
   * @endpoint GET http://localhost:8080/api/get-user-with-id
   */
  getUserWithId(userId: string): Observable<ResponseUser> {
    return this.http.get<ResponseUser>('http://localhost:8080/api/get-user-with-id', { params: { userId } });
  }

  /**
   * Verifica se o usuário atual possui privilégios de administrador
   * @returns Observable<boolean> - true se for administrador, false caso contrário
   * @endpoint GET http://localhost:8080/api/verify-if-user-admin
   */
  verifyIfUserIsAdm(): Observable<boolean> {
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
  checkUserVerification(): Observable<boolean> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<boolean>('http://localhost:8080/api/check-user-verification', { headers });
  }

  /**
   * Verifica se o usuário possui cartão e retorna seu status atual
   * @returns Observable<string> - Status do cartão: "EMPTY", "APPROVED", "CANCELED" ou "BLOCKED"
   * @endpoint GET http://localhost:8083/api/verify-if-user-has-card-and-your-status
   */
  verifyIfUserHasCardAndYourStatus(): Observable<string> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get('http://localhost:8083/api/verify-if-user-has-card-and-your-status', { headers, responseType: 'text' });
  }

  /**
   * Obtém os dados completos do cartão do usuário
   * @returns Observable com os dados do cartão
   * @endpoint GET http://localhost:8083/api/get-user-card
   */
  getUserCard(): Observable<ResponseUserCard> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<ResponseUserCard>('http://localhost:8083/api/get-user-card', { headers });
  }

  /**
   * Verifica o status atual dos documentos de identificação do usuário
   * @returns Observable<string> - Status do documento: "SEND", "PENDING", "APPROVED" ou "REJECTED"
   * @endpoint GET http://localhost:8081/api/check-document-status
   */
  checkDocumentStatus(): Observable<string> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<{ [key: string]: string }>('http://localhost:8081/api/check-document-status', { headers }).pipe(
      map(response => {
        const firstKey = Object.keys(response)[0];
        return response[firstKey];
      }),
      catchError((err: HttpErrorResponse) => {
        let errorMsg: string;
        if (err.error && typeof err.error === 'object') {
          const keys = Object.keys(err.error);
          if (keys.length > 0) {
            errorMsg = err.error[keys[0]];
          }
        }
        return throwError(() => new Error(errorMsg));
      })
    );
  }

  /**
   * Verifica o status atual dos documentos de crédito do usuário
   * @returns Observable<string> - Status do documento de crédito: "SEND", "PENDING", "APPROVED" ou "REJECTED"
   * @endpoint GET http://localhost:8081/api/check-credit-document-status
   */
  checkCreditDocumentStatus(): Observable<string> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.get<{ [key: string]: string }>('http://localhost:8081/api/check-credit-document-status', { headers }).pipe(
      map(response => {
        const firstKey = Object.keys(response)[0];
        return response[firstKey];
      }),
      catchError((err: HttpErrorResponse) => {
        let errorMsg: string;
        if (err.error && typeof err.error === 'object') {
          const keys = Object.keys(err.error);
          if (keys.length > 0) {
            errorMsg = err.error[keys[0]];
          }
        }
        return throwError(() => new Error(errorMsg));
      })
    );
  }

  /**
   * Envia documentos para análise de identificação
   * @param request - FormData com os documentos: fullName, rg, cpf, proofOfAddress, proofOfIncome
   * @returns Observable<string> - Mensagem de confirmação
   * @endpoint POST http://localhost:8080/api/document
   */
  documentsForAnalysis(request: any): Observable<string> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.post<{ [key: string]: string }>('http://localhost:8081/api/document', request, { headers }).pipe(
      map(response => {
        const firstKey = Object.keys(response)[0];
        return response[firstKey];
      }),
      catchError((err: HttpErrorResponse) => {
        let errorMsg: string;
        if (err.error && typeof err.error === 'object') {
          const keys = Object.keys(err.error);
          if (keys.length > 0) {
            errorMsg = err.error[keys[0]];
          }
        }
        console.error('Erro detalhado:', err);
        return throwError(() => new Error(errorMsg));
      })
    );
  }

  /**
   * Envia documentos para análise de crédito
   * @param request - FormData com os documentos: userId, fullName, cpf, date, occupation, salary, proofOfIncome
   * @returns Observable<string> - Mensagem de confirmação
   * @endpoint POST http://localhost:8080/api/credit-document
   */
  creditDocumentsForAnalysis(request: any): Observable<string> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.post<{ [key: string]: string }>('http://localhost:8081/api/credit-document', request, { headers }).pipe(
      map(response => {
        const firstKey = Object.keys(response)[0];
        return response[firstKey];
      }),
      catchError((err: HttpErrorResponse) => {
        let errorMsg: string;
        if (err.error && typeof err.error === 'object') {
          const keys = Object.keys(err.error);
          if (keys.length > 0) {
            errorMsg = err.error[keys[0]];
          }
        }
        console.error('Erro detalhado:', err);
        return throwError(() => new Error(errorMsg));
      })
    );
  }

  /**
   * Reporta um roubo ou perda do cartão
   * @param request - Dados do roubo: dateOfTheft, timeOfTheft, locationOfTheft, transactionId, amountLost, description
   * @returns Observable<string> - Mensagem de confirmação
   * @endpoint POST http://localhost:8082/api/report-theft
   */
  reportTheft(request: any): Observable<string> {
    return this.http.post<{ [key: string]: string }>('http://localhost:8082/api/report-theft', request).pipe(
      map(response => {
        const firstKey = Object.keys(response)[0];
        return response[firstKey];
      }),
      catchError((err: HttpErrorResponse) => {
        let errorMsg: string;
        if (err.error && typeof err.error === 'object') {
          const keys = Object.keys(err.error);
          if (keys.length > 0) {
            errorMsg = err.error[keys[0]];
          }
        }
        return throwError(() => new Error(errorMsg));
      })
    );
  }

  /**
   * Bloqueia ou desbloqueia o cartão do usuário
   * @returns Observable<void>
   * @endpoint PUT http://localhost:8083/api/block-card
   */
  blockCard(): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    });
    return this.http.put<void>('http://localhost:8083/api/block-card', { headers });
  }

  /**
   * Obtém todas as notificações do usuário
   * @returns Observable<Notifications[]> - Lista de notificações
   * @endpoint GET http://localhost:8084/api/notifications
   */
  allNotifications(): Observable<Notifications[]> {
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
    return this.http.put<void>('http://localhost:8084/api/visualisation-notification', { headers });
  }

  /**
   * Obtém a contagem de notificações não visualizadas
   * @returns Observable<number> - Número de notificações não visualizadas
   * @endpoint GET http://localhost:8084/api/count-notification
   */
  countNotifications(): Observable<number> {
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
  ocultNotification(notificationId: number): Observable<void> {
    return this.http.post<void>('http://localhost:8084/api/occult-notification', { notificationId: notificationId });
  }
}

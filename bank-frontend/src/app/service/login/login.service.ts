import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { loginDto } from '../../dtos/login/loginDto';
import { RequestTokens } from '../../dtos/login/RequestTokens';
import { ResponseTokens } from '../../dtos/login/ResponseTokens';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private http:HttpClient) { }

  login(request:loginDto):Observable<ResponseTokens>{
    return this.http.post<ResponseTokens>('http://localhost:8085/api/login', request).pipe(

        catchError((err: HttpErrorResponse) => {
        let errorMsg = 'Serviço de login está temporiamente fora do ar';

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

  refreshTokens(request:RequestTokens):Observable<ResponseTokens>{
    return this.http.post<ResponseTokens>('http://localhost:8085/api/refresh-token',
      {accessToken: request.accessToken, refreshToken: request.refreshToken});
  }


  getIdWithCpf(cpf: string): Observable<string> {
    return this.http.get('http://localhost:8080/api/get-id-with-cpf', {
      params: { cpf },
      responseType: 'text'
    });
  }
}

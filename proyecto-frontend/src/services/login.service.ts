import { Injectable } from "@angular/core";
import { env } from "../environment/env";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject } from "rxjs/internal/BehaviorSubject";
import { DatosLogin } from "../models/dto/datos-login";

@Injectable({
  providedIn: 'root'
})
export class LoginService {
    private  apiUrl = `${env.apiUrl}/login`;

    private estaLogeado = new BehaviorSubject<boolean>(false);

    constructor(private http: HttpClient) {
        this.verificarLogin();
    }
    
    verificarLogin() {
        const token = localStorage.getItem('token');
        this.estaLogeado.next(!!token);
    }

    autenticarUsuario(credentials: { username: string; password: string }) {
        return this.http.post<DatosLogin>(this.apiUrl, credentials);
    }

}

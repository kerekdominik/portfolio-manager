import {HttpInterceptorFn, HttpRequest} from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import {inject} from '@angular/core';

export const authInterceptorFn: HttpInterceptorFn = (request: HttpRequest<any>, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token) {
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(request);
};

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-oauth2-redirect',
  standalone: true,
  template: `<p>Redirection is in progress</p>`
})
export class Oauth2RedirectComponent implements OnInit {

  constructor(private readonly route: ActivatedRoute, private readonly router: Router) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        localStorage.setItem('jwtToken', token);
        this.router.navigate(['/dashboard']).then(r => console.log(r));
      } else {
        this.router.navigate(['/login']).then(r => console.log(r));
      }
    });
  }
}

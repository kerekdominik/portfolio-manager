import { Component } from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {MatToolbar} from '@angular/material/toolbar';
import {MatButton} from '@angular/material/button';
import {NgIf} from '@angular/common';
import {MenuComponent} from './shared/components/menu/menu.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MatToolbar, MatButton, RouterLink, NgIf, MenuComponent],
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'portfolio-frontend';
}

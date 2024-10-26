import { NgModule } from '@angular/core';
import {AppRoutingModule} from './app-routing.module';
import {CoreModule} from './core/core.module';
import {SharedModule} from './shared/shared.module';
import {BrowserModule} from '@angular/platform-browser';

@NgModule({
  declarations: [
  ],
  imports: [
    BrowserModule,
    CoreModule,
    SharedModule,
    AppRoutingModule
  ]
})
export class AppModule { }

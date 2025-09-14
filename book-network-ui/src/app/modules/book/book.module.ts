import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BookRoutingModule } from './book-routing.module';
import { MainComponent } from './pages/main/main.component';
import { BookListComponent } from './pages/book-list/book-list.component';

@NgModule({
  declarations: [
    // No need to declare standalone components here
  ],
  imports: [
    CommonModule,
    BookRoutingModule,
    // Import standalone components here
    MainComponent,
    BookListComponent
  ]
})
export class BookModule { }

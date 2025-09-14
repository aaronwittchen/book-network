import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthenticationService } from '../../services/services/authentication.service';
import { CodeInputModule } from 'angular-code-input';

@Component({
  selector: 'app-activate-account',
  standalone: true,
  imports: [CommonModule, RouterModule, CodeInputModule],
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.scss'],
})
export class ActivateAccountComponent {
  message = '';
  isOkay = true;
  submitted = false;

  constructor(
    private router: Router,
    private authService: AuthenticationService
  ) {}

  private confirmAccount(token: string) {
    this.authService.activate({ token }).subscribe({
      next: () => {
        this.message =
          'Your account has been successfully activated.\nNow you can proceed to login';
        this.submitted = true;
      },
      error: (err) => {
        this.message = err?.error?.message ?? 'Token has expired or is invalid';
        this.submitted = true;
        this.isOkay = false;
      },
    });
  }

  redirectToLogin() {
    this.router.navigate(['login']);
  }

  onCodeCompleted(token: string) {
    this.confirmAccount(token);
  }
}

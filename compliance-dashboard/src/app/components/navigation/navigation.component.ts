// navigation.component.ts - FIXED VERSION (Removed unnecessary elements)
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navigation',
  template: `
    <mat-toolbar class="navbar">
      <div class="nav-container">
        <div class="brand">
          <mat-icon class="brand-icon">security</mat-icon>
          <span class="brand-text">ComplianceVault</span>
        </div>

        <nav class="nav-links">
          <button
            mat-button
            class="nav-button"
            [class.active]="isActive('/search')"
            (click)="navigate('/search')">
            <mat-icon>search</mat-icon>
            <span>Message Search</span>
          </button>

          <button
            mat-button
            class="nav-button"
            [class.active]="isActive('/compliance')"
            (click)="navigate('/compliance')">
            <mat-icon>policy</mat-icon>
            <span>Compliance Policy</span>
          </button>
        </nav>

        <div class="brand-tagline">
          <span class="tagline-text">Enterprise Compliance Platform</span>
        </div>
      </div>
    </mat-toolbar>
  `,
  styles: [`
    .navbar {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-bottom: 1px solid rgba(255, 255, 255, 0.2);
      box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
      color: #333;
      height: 70px;
      padding: 0 20px;
    }

    .nav-container {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      max-width: 1400px;
      margin: 0 auto;
    }

    .brand {
      display: flex;
      align-items: center;
      gap: 12px;
      font-weight: 700;
      font-size: 20px;
    }

    .brand-icon {
      font-size: 28px;
      color: #667eea;
    }

    .brand-text {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .nav-links {
      display: flex;
      gap: 8px;
    }

    .nav-button {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 20px;
      border-radius: 25px;
      font-weight: 500;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
    }

    .nav-button:hover {
      background: rgba(102, 126, 234, 0.1);
      transform: translateY(-2px);
    }

    .nav-button.active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    }

    .nav-button.active::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0.1) 100%);
      pointer-events: none;
    }

    .brand-tagline {
      display: flex;
      align-items: center;
    }

    .tagline-text {
      font-size: 0.85rem;
      color: #667eea;
      font-weight: 500;
      opacity: 0.8;
    }

    @media (max-width: 768px) {
      .nav-container {
        padding: 0 10px;
      }

      .brand-text {
        display: none;
      }

      .tagline-text {
        display: none;
      }

      .nav-button span {
        display: none;
      }
    }

    @media (max-width: 480px) {
      .nav-button {
        padding: 8px 12px;
        gap: 4px;
      }

      .brand {
        font-size: 16px;
      }

      .brand-icon {
        font-size: 24px;
      }
    }
  `]
})
export class NavigationComponent {

  constructor(private router: Router) {}

  navigate(route: string): void {
    this.router.navigate([route]);
  }

  isActive(route: string): boolean {
    return this.router.url === route;
  }
}

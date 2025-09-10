// compliance-policy.component.ts - FIXED VERSION with Better Dropdown
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ComplianceService, CompliancePolicy, FlaggedMessage } from '../../services/compliance.service';

@Component({
  selector: 'app-compliance-policy',
  template: `
    <div class="compliance-container">
      <!-- Header Section -->
      <div class="compliance-header">
        <div class="header-content">
          <h1 class="page-title">
            <mat-icon class="title-icon">policy</mat-icon>
            Compliance Policy Dashboard
          </h1>
          <p class="page-subtitle">Monitor and review policy violations from MongoDB policies and PostgreSQL violations</p>
        </div>
      </div>

      <!-- Policy Selection Card -->
      <mat-card class="selection-card">
        <mat-card-header>
          <mat-card-title class="card-title">
            <mat-icon>rule</mat-icon>
            Select Compliance Policy
          </mat-card-title>
        </mat-card-header>

        <mat-card-content>
          <div class="selection-form">
            <mat-form-field appearance="outline" class="policy-select">
              <mat-label>Choose Policy</mat-label>
              <mat-select [(value)]="selectedPolicyName"
                         (selectionChange)="onPolicyChange()"
                         [panelClass]="'policy-dropdown-panel'">
                <mat-option value="">
                  <div class="policy-option-content">
                    <div class="policy-main-info">
                      <span class="policy-name">All Policies</span>
                    </div>
                  </div>
                </mat-option>
                <mat-option *ngFor="let policy of compliancePolicies" [value]="policy.name">
                  <div class="policy-option-content">
                    <div class="policy-main-info">
                      <span class="policy-name">{{policy.name}}</span>
                      <mat-chip class="severity-chip-small" [ngClass]="'severity-' + policy.severity.toLowerCase()">
                        {{policy.severity}}
                      </mat-chip>
                    </div>
                    <div class="policy-secondary-info">
                      <span class="policy-network">{{policy.network}}</span>
                      <span class="policy-status" [ngClass]="policy.enabled ? 'enabled' : 'disabled'">
                        {{policy.enabled ? 'Active' : 'Inactive'}}
                      </span>
                    </div>
                  </div>
                </mat-option>
              </mat-select>
              <mat-icon matSuffix>policy</mat-icon>
            </mat-form-field>

            <div class="action-buttons">
              <button mat-raised-button
                      color="primary"
                      (click)="loadViolations()"
                      [disabled]="loading"
                      class="load-btn">
                <mat-icon>refresh</mat-icon>
                {{loading ? 'Loading...' : 'Load Violations'}}
              </button>

              <button mat-stroked-button
                      (click)="exportViolations()"
                      [disabled]="violations.data.length === 0"
                      class="export-btn">
                <mat-icon>download</mat-icon>
                Export Results
              </button>
            </div>
          </div>
        </mat-card-content>
      </mat-card>

      <!-- Statistics Cards -->
      <div class="stats-grid" *ngIf="violations.data.length > 0">
        <mat-card class="stat-card total-violations">
          <mat-card-content>
            <div class="stat-content">
              <div class="stat-icon">
                <mat-icon>warning</mat-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{totalViolations}}</div>
                <div class="stat-label">Total Violations</div>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <mat-card class="stat-card pending-reviews">
          <mat-card-content>
            <div class="stat-content">
              <div class="stat-icon">
                <mat-icon>pending</mat-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{pendingReviews}}</div>
                <div class="stat-label">Pending Reviews</div>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <mat-card class="stat-card reviewed-count">
          <mat-card-content>
            <div class="stat-content">
              <div class="stat-icon">
                <mat-icon>check_circle</mat-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{reviewedCount}}</div>
                <div class="stat-label">Reviewed</div>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <mat-card class="stat-card high-severity">
          <mat-card-content>
            <div class="stat-content">
              <div class="stat-icon">
                <mat-icon>priority_high</mat-icon>
              </div>
              <div class="stat-info">
                <div class="stat-number">{{highSeverityCount}}</div>
                <div class="stat-label">High Severity</div>
              </div>
            </div>
          </mat-card-content>  <!-- ✅ correct -->
        </mat-card>

      </div>

      <!-- Connection Status Alert -->
      <mat-card class="status-card" *ngIf="!policiesLoaded || connectionError">
        <mat-card-content>
          <div class="status-content">
            <mat-icon class="status-icon" [ngClass]="connectionError ? 'error' : 'warning'">
              {{connectionError ? 'error' : 'warning'}}
            </mat-icon>
            <div class="status-text">
              <h3>{{connectionError ? 'Connection Error' : 'Loading Policies'}}</h3>
              <p *ngIf="connectionError">{{connectionErrorMessage}}</p>
              <p *ngIf="!policiesLoaded && !connectionError">Loading compliance policies from MongoDB...</p>
            </div>
          </div>
        </mat-card-content>
      </mat-card>

      <!-- Violations Table -->
      <mat-card class="violations-card" *ngIf="violations.data.length > 0 || loading">
        <mat-card-header>
          <mat-card-title class="card-title">
            <mat-icon>flag</mat-icon>
            Policy Violations
            <mat-chip class="results-count" *ngIf="totalViolations > 0">
              {{totalViolations}} violations found
            </mat-chip>
          </mat-card-title>
        </mat-card-header>

        <mat-card-content>
          <!-- Loading Spinner -->
          <div class="loading-container" *ngIf="loading">
            <mat-spinner diameter="50"></mat-spinner>
            <p>Loading policy violations from PostgreSQL...</p>
          </div>

          <!-- Violations Table -->
          <div class="table-container" *ngIf="!loading && violations.data.length > 0">
            <table mat-table [dataSource]="violations" class="violations-table">
              <!-- Message ID Column -->
              <ng-container matColumnDef="messageId">
                <th mat-header-cell *matHeaderCellDef class="header-cell">Message ID</th>
                <td mat-cell *matCellDef="let violation" class="data-cell">
                  <div class="message-id">
                    <mat-icon class="cell-icon">tag</mat-icon>
                    <span class="message-id-text">{{violation.messageId}}</span>
                  </div>
                </td>
              </ng-container>

              <!-- Violated Policy Column -->
              <ng-container matColumnDef="violatedPolicy">
                <th mat-header-cell *matHeaderCellDef class="header-cell">Violated Policy</th>
                <td mat-cell *matCellDef="let violation" class="data-cell">
                  <div class="policy-info">
                    <mat-icon class="cell-icon">policy</mat-icon>
                    <span class="policy-name">{{violation.violatedPolicy}}</span>
                  </div>
                </td>
              </ng-container>

              <!-- Network Column -->
              <ng-container matColumnDef="network">
                <th mat-header-cell *matHeaderCellDef class="header-cell">Network</th>
                <td mat-cell *matCellDef="let violation" class="data-cell">
                  <mat-chip class="network-chip" [ngClass]="'network-' + violation.network">
                    <mat-icon>{{getNetworkIcon(violation.network)}}</mat-icon>
                    {{violation.network | titlecase}}
                  </mat-chip>
                </td>
              </ng-container>

              <!-- Severity Column -->
              <ng-container matColumnDef="severity">
                <th mat-header-cell *matHeaderCellDef class="header-cell">Severity</th>
                <td mat-cell *matCellDef="let violation" class="data-cell">
                  <mat-chip class="severity-chip" [ngClass]="'severity-' + violation.severity.toLowerCase()">
                    <mat-icon>{{getSeverityIcon(violation.severity)}}</mat-icon>
                    {{violation.severity}}
                  </mat-chip>
                </td>
              </ng-container>

              <!-- Content Preview Column -->
              <ng-container matColumnDef="content">
                <th mat-header-cell *matHeaderCellDef class="header-cell">Content Preview</th>
                <td mat-cell *matCellDef="let violation" class="data-cell">
                  <div class="content-preview">
                    <span class="content-text">{{getContentPreview(violation.content)}}</span>
                    <button mat-icon-button
                            (click)="viewFullContent(violation)"
                            matTooltip="View Full Content"
                            class="view-content-btn">
                      <mat-icon>visibility</mat-icon>
                    </button>
                  </div>
                </td>
              </ng-container>

              <!-- Timestamp Column -->
              <ng-container matColumnDef="timestamp">
                <th mat-header-cell *matHeaderCellDef class="header-cell">Detected At</th>
                <td mat-cell *matCellDef="let violation" class="data-cell">
                  <div class="timestamp-info">
                    <mat-icon class="cell-icon">access_time</mat-icon>
                    <span>{{formatDate(violation.timestamp)}}</span>
                  </div>
                </td>
              </ng-container>

              <!-- Review Status Column -->
              <ng-container matColumnDef="reviewStatus">
                <th mat-header-cell *matHeaderCellDef class="header-cell">Review Status</th>
                <td mat-cell *matCellDef="let violation" class="data-cell">
                  <mat-chip class="status-chip" [ngClass]="getStatusClass(violation.reviewStatus)">
                    <mat-icon>{{getStatusIcon(violation.reviewStatus)}}</mat-icon>
                    {{violation.reviewStatus || 'PENDING'}}
                  </mat-chip>
                </td>
              </ng-container>

              <!-- Actions Column -->
              <ng-container matColumnDef="actions">
                <th mat-header-cell *matHeaderCellDef class="header-cell">Actions</th>
                <td mat-cell *matCellDef="let violation" class="data-cell">
                  <div class="action-buttons">
                    <button mat-icon-button
                            color="primary"
                            [matMenuTriggerFor]="actionMenu"
                            [matMenuTriggerData]="{violation: violation}"
                            matTooltip="Actions">
                      <mat-icon>more_vert</mat-icon>
                    </button>

                    <mat-menu #actionMenu="matMenu">
                      <ng-template matMenuContent let-violation="violation">
                        <button mat-menu-item (click)="markAsReviewed(violation)">
                          <mat-icon>check</mat-icon>
                          <span>Mark as Reviewed</span>
                        </button>
                        <button mat-menu-item (click)="markAsPending(violation)">
                          <mat-icon>schedule</mat-icon>
                          <span>Mark as Pending</span>
                        </button>
                        <button mat-menu-item (click)="viewViolationDetails(violation)">
                          <mat-icon>info</mat-icon>
                          <span>View Details</span>
                        </button>
                      </ng-template>
                    </mat-menu>
                  </div>
                </td>
              </ng-container>

              <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
              <tr mat-row *matRowDef="let row; columns: displayedColumns;"
                  class="table-row"
                  [class.high-severity-row]="row.severity === 'HIGH'"
                  [class.reviewed-row]="row.reviewStatus === 'REVIEWED'"></tr>
            </table>

            <!-- Paginator -->
            <mat-paginator
              [length]="totalViolations"
              [pageSize]="pageSize"
              [pageSizeOptions]="[10, 20, 50, 100]"
              (page)="onPageChange($event)"
              showFirstLastButtons
              class="paginator">
            </mat-paginator>
          </div>

          <!-- No Results -->
          <div class="no-results" *ngIf="!loading && violations.data.length === 0 && searchPerformed">
            <mat-icon class="no-results-icon">policy_off</mat-icon>
            <h3>No policy violations found</h3>
            <p *ngIf="selectedPolicyName">No violations found for policy "{{selectedPolicyName}}"</p>
            <p *ngIf="!selectedPolicyName">No policy violations found in PostgreSQL database</p>
          </div>
        </mat-card-content>
      </mat-card>

      <!-- Policy Information Card -->
      <mat-card class="policy-info-card" *ngIf="selectedPolicyDetails">
        <mat-card-header>
          <mat-card-title class="card-title">
            <mat-icon>info</mat-icon>
            Policy Information
          </mat-card-title>
        </mat-card-header>

        <mat-card-content>
          <div class="policy-details-grid">
            <div class="detail-item">
              <label>Policy Name:</label>
              <span>{{selectedPolicyDetails.name}}</span>
            </div>
            <div class="detail-item">
              <label>Description:</label>
              <span>{{selectedPolicyDetails.description}}</span>
            </div>
            <div class="detail-item">
              <label>Network:</label>
              <span>{{selectedPolicyDetails.network}}</span>
            </div>
            <div class="detail-item">
              <label>Severity:</label>
              <mat-chip class="severity-chip" [ngClass]="'severity-' + selectedPolicyDetails.severity.toLowerCase()">
                {{selectedPolicyDetails.severity}}
              </mat-chip>
            </div>
            <div class="detail-item">
              <label>Status:</label>
              <mat-chip [class]="selectedPolicyDetails.enabled ? 'enabled-chip' : 'disabled-chip'">
                {{selectedPolicyDetails.enabled ? 'Enabled' : 'Disabled'}}
              </mat-chip>
            </div>
            <div class="detail-item regex-item">
              <label>Regex Pattern:</label>
              <code class="regex-code">{{selectedPolicyDetails.regex}}</code>
            </div>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .compliance-container {
      max-width: 1400px;
      margin: 0 auto;
      padding: 20px;
    }

    .compliance-header {
      margin-bottom: 30px;
    }

    .header-content {
      text-align: center;
      color: white;
    }

    .page-title {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 15px;
      font-size: 2.5rem;
      font-weight: 700;
      margin: 0;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    }

    .title-icon {
      font-size: 2.5rem;
      width: 2.5rem;
      height: 2.5rem;
    }

    .page-subtitle {
      font-size: 1.2rem;
      opacity: 0.9;
      margin: 10px 0 0 0;
    }

    .selection-card, .violations-card, .policy-info-card, .status-card {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-radius: 20px;
      box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
      border: 1px solid rgba(255, 255, 255, 0.18);
      margin-bottom: 25px;
    }

    .card-title {
      display: flex;
      align-items: center;
      gap: 10px;
      color: #667eea;
      font-weight: 600;
    }

    .selection-form {
      padding: 20px 0;
    }

    .policy-select {
      width: 100%;
      margin-bottom: 20px;
    }

    /* FIXED: Better Dropdown Styling */
    .policy-option-content {
      display: flex;
      flex-direction: column;
      gap: 8px;
      width: 100%;
      padding: 8px 0;
    }

    .policy-main-info {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 10px;
    }

    .policy-name {
      font-weight: 600;
      color: #333;
      font-size: 14px;
      flex: 1;
    }

    .policy-secondary-info {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 12px;
      color: #666;
    }

    .policy-network {
      background: #f5f5f5;
      padding: 4px 8px;
      border-radius: 8px;
      font-size: 11px;
      border: 1px solid #e0e0e0;
    }

    .policy-status.enabled {
      color: #2e7d32;
      font-weight: 500;
    }

    .policy-status.disabled {
      color: #d32f2f;
      font-weight: 500;
    }

    .severity-chip-small {
      font-size: 10px !important;
      padding: 2px 6px !important;
      height: 20px !important;
      min-height: 20px !important;
      border-radius: 10px !important;
    }

    /* FIXED: Custom dropdown panel styling */
    ::ng-deep .policy-dropdown-panel {
      max-height: 300px !important;
      border-radius: 12px !important;
      box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37) !important;
      background: white !important;
    }

    ::ng-deep .policy-dropdown-panel .mat-option {
      height: auto !important;
      min-height: 48px !important;
      padding: 12px 16px !important;
      line-height: 1.4 !important;
      border-bottom: 1px solid #f0f0f0 !important;
    }

    ::ng-deep .policy-dropdown-panel .mat-option:hover {
      background-color: rgba(102, 126, 234, 0.08) !important;
    }

    ::ng-deep .policy-dropdown-panel .mat-option.mat-selected {
      background-color: rgba(102, 126, 234, 0.12) !important;
      color: #667eea !important;
    }

    .action-buttons {
      display: flex;
      gap: 15px;
      justify-content: center;
    }

    .load-btn {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 25px;
      padding: 12px 30px;
      font-weight: 600;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    }

    .export-btn {
      border-radius: 25px;
      padding: 12px 30px;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 25px;
    }

    .stat-card {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-radius: 15px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      transition: transform 0.3s ease;
    }

    .stat-card:hover {
      transform: translateY(-5px);
    }

    .stat-content {
      display: flex;
      align-items: center;
      gap: 15px;
      padding: 15px;
    }

    .stat-icon {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .total-violations .stat-icon {
      background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
      color: white;
    }

    .pending-reviews .stat-icon {
      background: linear-gradient(135deg, #ffd93d, #ffe066);
      color: white;
    }

    .reviewed-count .stat-icon {
      background: linear-gradient(135deg, #6bcf7f, #8ed993);
      color: white;
    }

    .high-severity .stat-icon {
      background: linear-gradient(135deg, #ff4757, #ff6b7d);
      color: white;
    }

    .stat-number {
      font-size: 2rem;
      font-weight: 700;
      color: #333;
    }

    .stat-label {
      font-size: 0.9rem;
      color: #666;
      margin-top: 5px;
    }

    /* Status Card Styling */
    .status-content {
      display: flex;
      align-items: center;
      gap: 15px;
      padding: 20px;
    }

    .status-icon {
      font-size: 2rem;
      width: 2rem;
      height: 2rem;
    }

    .status-icon.error {
      color: #f44336;
    }

    .status-icon.warning {
      color: #ff9800;
    }

    .status-text h3 {
      margin: 0 0 5px 0;
      color: #333;
      font-weight: 600;
    }

    .status-text p {
      margin: 0;
      color: #666;
      font-size: 0.9rem;
    }

    .results-count {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      margin-left: 15px;
    }

    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 50px;
      color: #666;
    }

    .table-container {
      overflow-x: auto;
    }

    .violations-table {
      width: 100%;
      border-radius: 10px;
      overflow: hidden;
    }

    .header-cell {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      font-weight: 600;
      padding: 15px;
    }

    .data-cell {
      padding: 15px;
      border-bottom: 1px solid #f0f0f0;
    }

    .table-row {
      transition: background-color 0.3s ease;
    }

    .table-row:hover {
      background-color: rgba(102, 126, 234, 0.05);
    }

    .high-severity-row {
      background-color: rgba(255, 75, 87, 0.05);
      border-left: 4px solid #ff4757;
    }

    .reviewed-row {
      background-color: rgba(107, 207, 127, 0.05);
      border-left: 4px solid #6bcf7f;
    }

    .message-id, .policy-info, .timestamp-info {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .message-id-text {
      font-family: monospace;
      font-size: 0.9rem;
      color: #666;
    }

    .cell-icon {
      font-size: 16px;
      width: 16px;
      height: 16px;
      color: #666;
    }

    .policy-name {
      font-weight: 500;
    }

    .network-chip, .severity-chip, .status-chip {
      display: flex;
      align-items: center;
      gap: 5px;
      border-radius: 15px;
      font-size: 12px;
      font-weight: 500;
    }

    .network-email {
      background-color: #e3f2fd;
      color: #1565c0;
    }

    .network-slack {
      background-color: #f3e5f5;
      color: #7b1fa2;
    }

    .severity-low {
      background-color: #e8f5e8;
      color: #2e7d32;
    }

    .severity-medium {
      background-color: #fff3e0;
      color: #ef6c00;
    }

    .severity-high {
      background-color: #ffebee;
      color: #c62828;
    }

    .status-pending {
      background-color: #fff3e0;
      color: #ef6c00;
    }

    .status-reviewed {
      background-color: #e8f5e8;
      color: #2e7d32;
    }

    .content-preview {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .content-text {
      max-width: 200px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 0.9rem;
    }

    .view-content-btn {
      color: #667eea;
    }

    .action-buttons {
      display: flex;
      gap: 5px;
    }

    .paginator {
      border-top: 1px solid #e0e0e0;
      margin-top: 20px;
    }

    .no-results {
      text-align: center;
      padding: 50px;
      color: #666;
    }

    .no-results-icon {
      font-size: 4rem;
      width: 4rem;
      height: 4rem;
      margin-bottom: 20px;
      opacity: 0.5;
    }

    .policy-details-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 20px;
      padding: 20px 0;
    }

    .detail-item {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .detail-item label {
      font-weight: 600;
      color: #667eea;
      font-size: 0.9rem;
    }

    .detail-item span {
      font-size: 1rem;
      color: #333;
    }

    .regex-item {
      grid-column: 1 / -1;
    }

    .regex-code {
      background: #f5f5f5;
      padding: 10px;
      border-radius: 8px;
      font-family: 'Courier New', monospace;
      font-size: 0.9rem;
      color: #d63384;
      word-break: break-all;
    }

    .enabled-chip {
      background-color: #e8f5e8;
      color: #2e7d32;
    }

    .disabled-chip {
      background-color: #ffebee;
      color: #c62828;
    }

    @media (max-width: 768px) {
      .stats-grid {
        grid-template-columns: 1fr;
      }

      .page-title {
        font-size: 2rem;
      }

      .action-buttons {
        flex-direction: column;
        align-items: center;
      }

      .load-btn, .export-btn {
        width: 100%;
        max-width: 300px;
      }

      .policy-details-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class CompliancePolicyComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  violations = new MatTableDataSource<FlaggedMessage>([]);
  displayedColumns: string[] = ['messageId', 'violatedPolicy', 'network', 'severity', 'content', 'timestamp', 'reviewStatus', 'actions'];

  compliancePolicies: CompliancePolicy[] = [];
  selectedPolicyName = '';
  selectedPolicyDetails: CompliancePolicy | null = null;

  totalViolations = 0;
  pendingReviews = 0;
  reviewedCount = 0;
  highSeverityCount = 0;

  pageSize = 20;
  currentPage = 0;
  loading = false;
  searchPerformed = false;
  policiesLoaded = false;
  connectionError = false;
  connectionErrorMessage = '';

  // Local storage for review statuses (since backend doesn't have this field yet)
  private reviewStatuses = new Map<string, 'PENDING' | 'REVIEWED'>();

  constructor(
    private complianceService: ComplianceService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    console.log('CompliancePolicyComponent: Initializing...');
    this.loadCompliancePolicies();
    this.loadAllViolations();
  }

  loadCompliancePolicies(): void {
    console.log('CompliancePolicyComponent: Loading compliance policies from MongoDB...');
    this.loading = true;
    this.connectionError = false;

    this.complianceService.getCompliancePolicies().subscribe({
      next: (policies) => {
        console.log('✅ Successfully loaded compliance policies:', policies);
        this.compliancePolicies = policies;
        this.policiesLoaded = true;
        this.loading = false;

        if (policies.length === 0) {
          this.showSnackBar('No compliance policies found in MongoDB. Please add some policies first.');
        } else {
          this.showSnackBar(`✅ Loaded ${policies.length} compliance policies from MongoDB`);
        }
      },
      error: (error) => {
        console.error('❌ Error loading compliance policies:', error);
        this.loading = false;
        this.connectionError = true;
        this.connectionErrorMessage = this.getErrorMessage(error);
        this.showSnackBar('❌ Failed to load policies from MongoDB. Check console for details.');
      }
    });
  }

  loadAllViolations(): void {
    console.log('CompliancePolicyComponent: Loading all flagged messages from PostgreSQL...');
    this.loading = true;
    this.searchPerformed = true;

    this.complianceService.getAllFlagged().subscribe({
      next: (violations) => {
        console.log('✅ Successfully loaded violations from PostgreSQL:', violations);

        // Add mock review statuses to violations
        const violationsWithStatus = violations.map(v => ({
          ...v,
          reviewStatus: this.getOrCreateReviewStatus(v.messageId)
        }));

        this.violations.data = violationsWithStatus;
        this.totalViolations = violations.length;
        this.calculateStats(violationsWithStatus);
        this.loading = false;

        if (violations.length === 0) {
          this.showSnackBar('No policy violations found in PostgreSQL database');
        } else {
          this.showSnackBar(`✅ Loaded ${violations.length} policy violations from PostgreSQL`);
        }
      },
      error: (error) => {
        console.error('❌ Error loading violations from PostgreSQL:', error);
        this.loading = false;
        this.connectionError = true;
        this.connectionErrorMessage = this.getErrorMessage(error);
        this.showSnackBar('❌ Failed to load violations from PostgreSQL. Check console for details.');
      }
    });
  }

  onPolicyChange(): void {
    console.log('CompliancePolicyComponent: Policy changed to:', this.selectedPolicyName);
    if (this.selectedPolicyName) {
      this.selectedPolicyDetails = this.compliancePolicies.find(p => p.name === this.selectedPolicyName) || null;
      console.log('Selected policy details:', this.selectedPolicyDetails);
    } else {
      this.selectedPolicyDetails = null;
    }
  }

  loadViolations(): void {
    console.log('CompliancePolicyComponent: Loading violations for policy:', this.selectedPolicyName);
    this.loading = true;
    this.searchPerformed = true;

    if (this.selectedPolicyName) {
      // Filter by specific policy
      this.complianceService.getFlaggedByPolicy(this.selectedPolicyName).subscribe({
        next: (violations) => {
          console.log(`✅ Found ${violations.length} violations for policy "${this.selectedPolicyName}":`, violations);

          const violationsWithStatus = violations.map(v => ({
            ...v,
            reviewStatus: this.getOrCreateReviewStatus(v.messageId)
          }));

          this.violations.data = violationsWithStatus;
          this.totalViolations = violations.length;
          this.calculateStats(violationsWithStatus);
          this.loading = false;

          if (violations.length === 0) {
            this.showSnackBar(`No violations found for policy "${this.selectedPolicyName}"`);
          } else {
            this.showSnackBar(`✅ Found ${violations.length} violations for policy "${this.selectedPolicyName}"`);
          }
        },
        error: (error) => {
          console.error(`❌ Error loading filtered violations for policy "${this.selectedPolicyName}":`, error);
          this.loading = false;
          this.showSnackBar('❌ Error loading filtered violations from PostgreSQL');
        }
      });
    } else {
      // Load all violations
      this.loadAllViolations();
    }
  }

  private getOrCreateReviewStatus(messageId: string): 'PENDING' | 'REVIEWED' {
    if (!this.reviewStatuses.has(messageId)) {
      // Randomly assign status for demo purposes (70% pending, 30% reviewed)
      const status = Math.random() > 0.3 ? 'PENDING' : 'REVIEWED';
      this.reviewStatuses.set(messageId, status);
    }
    return this.reviewStatuses.get(messageId)!;
  }

  private calculateStats(violations: any[]): void {
    this.pendingReviews = violations.filter(v => v.reviewStatus === 'PENDING').length;
    this.reviewedCount = violations.filter(v => v.reviewStatus === 'REVIEWED').length;
    this.highSeverityCount = violations.filter(v => v.severity?.toUpperCase() === 'HIGH').length;

    console.log('Stats calculated:', {
      total: this.totalViolations,
      pending: this.pendingReviews,
      reviewed: this.reviewedCount,
      highSeverity: this.highSeverityCount
    });
  }

  private getErrorMessage(error: any): string {
    if (error.status === 0) {
      return 'Cannot connect to compliance service (port 8082). Please ensure the service is running.';
    } else if (error.status === 404) {
      return 'API endpoint not found. Please check if the compliance service endpoints are properly configured.';
    } else if (error.status === 500) {
      return 'Internal server error. Please check the compliance service logs.';
    } else {
      return `HTTP ${error.status}: ${error.message || error.statusText || 'Unknown error'}`;
    }
  }

  markAsReviewed(violation: any): void {
    console.log('Marking violation as reviewed:', violation.messageId);
    this.reviewStatuses.set(violation.messageId, 'REVIEWED');
    violation.reviewStatus = 'REVIEWED';
    this.calculateStats(this.violations.data);
    this.showSnackBar('Violation marked as reviewed');

    // TODO: Implement backend update when review status endpoint is available
    // this.reviewService.updateReviewStatus(violation.id, 'REVIEWED').subscribe();
  }

  markAsPending(violation: any): void {
    console.log('Marking violation as pending:', violation.messageId);
    this.reviewStatuses.set(violation.messageId, 'PENDING');
    violation.reviewStatus = 'PENDING';
    this.calculateStats(this.violations.data);
    this.showSnackBar('Violation marked as pending');

    // TODO: Implement backend update when review status endpoint is available
    // this.reviewService.updateReviewStatus(violation.id, 'PENDING').subscribe();
  }

  viewViolationDetails(violation: any): void {
    console.log('View violation details:', violation);

    // Create a detailed view dialog or expand row
    const details = `
      Message ID: ${violation.messageId}
      Network: ${violation.network}
      Policy: ${violation.violatedPolicy}
      Severity: ${violation.severity}
      Content: ${violation.content}
      Timestamp: ${this.formatDate(violation.timestamp)}
      Review Status: ${violation.reviewStatus}
    `;

    alert(`Violation Details:\n\n${details}`);
    // TODO: Replace with proper dialog component
  }

  viewFullContent(violation: any): void {
    console.log('View full content:', violation.content);

    // Show full content in a dialog
    const content = violation.content || 'No content available';
    alert(`Full Content:\n\n${content}`);
    // TODO: Replace with proper dialog component
  }

  exportViolations(): void {
    if (this.violations.data.length === 0) {
      this.showSnackBar('No data to export');
      return;
    }

    console.log('Exporting violations:', this.violations.data.length);

    // Create CSV content
    const headers = ['Message ID', 'Violated Policy', 'Network', 'Severity', 'Content Preview', 'Timestamp', 'Review Status'];
    const csvContent = [
      headers.join(','),
      ...this.violations.data.map(v => [
        v.messageId,
        v.violatedPolicy,
        v.network,
        v.severity,
        `"${this.getContentPreview(v.content).replace(/"/g, '""')}"`,
        this.formatDate(v.timestamp),
        (v as any).reviewStatus || 'PENDING'
      ].join(','))
    ].join('\n');

    // Download CSV
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `compliance-violations-${new Date().toISOString().slice(0, 10)}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    this.showSnackBar('Violations exported successfully');
  }

  onPageChange(event: any): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    console.log('Page changed:', { page: this.currentPage, size: this.pageSize });
    // Note: Since we're loading all data at once, we don't need to refetch
    // In a real implementation, you would call loadViolations() here with pagination
  }

  getNetworkIcon(network: string): string {
    switch (network?.toLowerCase()) {
      case 'email': return 'email';
      case 'slack': return 'chat';
      default: return 'network_check';
    }
  }

  getSeverityIcon(severity: string): string {
    switch (severity?.toUpperCase()) {
      case 'HIGH': return 'priority_high';
      case 'MEDIUM': return 'warning';
      case 'LOW': return 'info';
      default: return 'help';
    }
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'REVIEWED': return 'check_circle';
      case 'PENDING': return 'schedule';
      default: return 'help';
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'REVIEWED': return 'status-reviewed';
      case 'PENDING': return 'status-pending';
      default: return 'status-pending';
    }
  }

  getContentPreview(content: string): string {
    if (!content) return 'No content';
    return content.length > 50 ? content.substring(0, 50) + '...' : content;
  }

  formatDate(timestamp: string): string {
    if (!timestamp) return 'N/A';
    const date = new Date(timestamp);
    return date.toLocaleDateString() + ' ' +
           date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  private showSnackBar(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom'
    });
  }
}

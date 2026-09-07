# Task00017 API Documentation

Base URL:

```text
http://localhost:8080/api/v1
```

Most endpoints require JWT authentication:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Public endpoints: register, login, forgot password, reset password.

Standard JSON response:

```json
{
  "success": true,
  "message": "Operation message",
  "data": {},
  "timestamp": "2026-09-07T12:00:00"
}
```

Validation and business errors return the same wrapper with `success=false`.

## Enums

| Enum | Values |
| --- | --- |
| `UserRole` | `ADMIN`, `USER` |
| `UserStatus` | `ACTIVE`, `LOCKED`, `DISABLED` |
| `WalletType` | `MAIN`, `SAVING`, `GOAL`, `EMERGENCY`, `SPENDING` |
| `WalletStatus` | `ACTIVE`, `ARCHIVED` |
| `CategoryType` | `INCOME`, `EXPENSE` |
| `TransactionType` | `INCOME`, `EXPENSE`, `TRANSFER` |
| `TransactionStatus` | `POSTED`, `PENDING`, `CANCELLED` |
| `PaymentMethod` | `CASH`, `BANK_TRANSFER`, `CARD`, `E_WALLET`, `OTHER` |
| `PeriodType` | `WEEKLY`, `MONTHLY`, `QUARTERLY`, `YEARLY`, `CUSTOM` |
| `BudgetStatus` | `ACTIVE`, `CLOSED`, `ARCHIVED`, `OVER_BUDGET`, `NEAR_LIMIT` |

## Authentication

### Register

```http
POST /auth/register
```

Request:

```json
{
  "username": "demo",
  "email": "demo@example.com",
  "phone": "0912345678",
  "password": "secret123",
  "fullName": "Demo User"
}
```

Response `data`:

```json
{
  "id": 1,
  "username": "demo",
  "email": "demo@example.com",
  "phone": "0912345678",
  "fullName": "Demo User",
  "avatarUrl": null,
  "role": "USER",
  "status": "ACTIVE"
}
```

### Login

```http
POST /auth/login
```

Request:

```json
{
  "username": "demo",
  "password": "secret123"
}
```

Response `data`:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "username": "demo",
    "email": "demo@example.com",
    "role": "USER",
    "status": "ACTIVE"
  }
}
```

### Logout

```http
POST /auth/logout
```

Requires authentication. The current JWT is added to the blacklist.

### Forgot Password

```http
POST /auth/forgot-password
```

Request:

```json
{
  "email": "demo@example.com"
}
```

Response `data`:

```json
{
  "resetToken": "123456"
}
```

### Reset Password

```http
POST /auth/reset-password
```

Request:

```json
{
  "email": "demo@example.com",
  "otp": "123456",
  "newPassword": "newsecret123"
}
```

## Users

### Get Current Profile

```http
GET /user/profile
```

Requires authentication.

Response `data` fields:

```json
{
  "id": 1,
  "username": "demo",
  "email": "demo@example.com",
  "phone": "0912345678",
  "fullName": "Demo User",
  "avatarUrl": null,
  "role": "USER",
  "status": "ACTIVE"
}
```

### Get All Profiles

```http
GET /user/all
```

Requires `ADMIN`.

### Update Profile

```http
PATCH /user/profile
```

Request:

```json
{
  "phone": "0987654321",
  "fullName": "Demo Updated",
  "avatarUrl": "https://example.com/avatar.png"
}
```

### Change Password

```http
PUT /user/change-password
```

Request:

```json
{
  "currentPassword": "secret123",
  "newPassword": "newsecret123"
}
```

## Wallets

### List Wallets

```http
GET /wallets/all
```

### Create Wallet

```http
POST /wallets/creation
```

Request:

```json
{
  "name": "Cash",
  "walletType": "MAIN",
  "currencyCode": "VND",
  "openingBalance": 100000,
  "targetAmount": null,
  "targetDate": null
}
```

For a goal wallet:

```json
{
  "name": "Buy laptop",
  "walletType": "GOAL",
  "currencyCode": "VND",
  "openingBalance": 0,
  "targetAmount": 20000000,
  "targetDate": "2026-12-31"
}
```

Response `data` fields:

```json
{
  "id": 1,
  "name": "Cash",
  "userId": 1,
  "walletType": "MAIN",
  "currencyCode": "VND",
  "openingBalance": 100000,
  "currentBalance": 100000,
  "targetAmount": null,
  "targetDate": null,
  "defaultWallet": true,
  "walletStatus": "ACTIVE",
  "createdAt": "2026-09-07T12:00:00",
  "updatedAt": "2026-09-07T12:00:00"
}
```

### Update Wallet

```http
PUT /wallets/{id}
```

Request:

```json
{
  "name": "Cash Updated",
  "walletType": "MAIN",
  "currencyCode": "VND",
  "openingBalance": 100000
}
```

### Delete Wallet

```http
DELETE /wallets/{id}
```

Funded non-main wallets cannot be deleted until their balance is zero.

## Categories

### List Categories

```http
GET /categories/all
```

Returns current user's categories and available system categories.

### Create Category

```http
POST /categories/creation
```

Request:

```json
{
  "name": "Food",
  "categoryType": "EXPENSE",
  "icon": "utensils",
  "color": "#22C55E"
}
```

Response `data` fields:

```json
{
  "id": 1,
  "name": "Food",
  "categoryType": "EXPENSE",
  "icon": "utensils",
  "color": "#22C55E",
  "system": false,
  "active": true,
  "createdAt": "2026-09-07T12:00:00",
  "updatedAt": "2026-09-07T12:00:00"
}
```

### Update Category

```http
PUT /categories/{id}
```

Request:

```json
{
  "name": "Food Updated",
  "categoryType": "EXPENSE",
  "icon": "utensils",
  "color": "#16A34A"
}
```

### Delete Category

```http
DELETE /categories/{id}
```

## Transactions

### List Transactions

```http
GET /transactions/all
```

Query parameters are optional:

| Parameter | Type | Example |
| --- | --- | --- |
| `walletId` | number | `1` |
| `type` | `TransactionType` | `EXPENSE` |
| `status` | `TransactionStatus` | `POSTED` |
| `fromDate` | date | `2026-09-01` |
| `toDate` | date | `2026-09-30` |

Example:

```http
GET /transactions/all?walletId=1&type=EXPENSE&status=POSTED&fromDate=2026-09-01&toDate=2026-09-30
```

Response `data` item fields:

```json
{
  "id": 1,
  "walletId": 1,
  "categoryId": 1,
  "transferWalletId": null,
  "transactionType": "EXPENSE",
  "amount": 30000,
  "currencyCode": "VND",
  "transactionDate": "2026-09-02",
  "title": "Lunch",
  "status": "POSTED"
}
```

### Get Transaction Detail

```http
GET /transactions/{id}
```

### Create Transaction

```http
POST /transactions/creation
```

Expense or income request:

```json
{
  "walletId": 1,
  "categoryId": 1,
  "transferWalletId": null,
  "transactionType": "EXPENSE",
  "amount": 30000,
  "currencyCode": "VND",
  "transactionDate": "2026-09-02",
  "title": "Lunch",
  "note": "Office lunch",
  "paymentMethod": "CASH",
  "status": "POSTED"
}
```

Transfer request:

```json
{
  "walletId": 1,
  "categoryId": 1,
  "transferWalletId": 2,
  "transactionType": "TRANSFER",
  "amount": 30000,
  "currencyCode": "VND",
  "transactionDate": "2026-09-02",
  "title": "Move to goal",
  "note": null,
  "paymentMethod": "BANK_TRANSFER",
  "status": "POSTED"
}
```

Response `data` includes readable names:

```json
{
  "id": 1,
  "userId": 1,
  "walletId": 1,
  "walletName": "Cash",
  "categoryId": 1,
  "categoryName": "Food",
  "transferWalletId": null,
  "transferWalletName": null,
  "transactionType": "EXPENSE",
  "amount": 30000,
  "currencyCode": "VND",
  "transactionDate": "2026-09-02",
  "title": "Lunch",
  "note": "Office lunch",
  "paymentMethod": "CASH",
  "status": "POSTED",
  "createdAt": "2026-09-07T12:00:00",
  "updatedAt": "2026-09-07T12:00:00"
}
```

### Update Transaction

```http
PUT /transactions/{id}
```

Request body is the same shape as create transaction.

### Delete Transaction

```http
DELETE /transactions/{id}
```

## Budgets

### List Budgets

```http
GET /budgets/all
```

### Create Budget

```http
POST /budgets/creation
```

Request:

```json
{
  "categoryId": 1,
  "name": "Monthly Food Budget",
  "limitAmount": 2000000,
  "currencyCode": "VND",
  "startDate": "2026-09-01",
  "endDate": "2026-09-30",
  "periodType": "MONTHLY"
}
```

Response `data` fields:

```json
{
  "id": 1,
  "userId": 1,
  "categoryId": 1,
  "categoryName": "Food",
  "name": "Monthly Food Budget",
  "limitAmount": 2000000,
  "spentAmount": 30000,
  "remainingAmount": 1970000,
  "currencyCode": "VND",
  "startDate": "2026-09-01",
  "endDate": "2026-09-30",
  "periodType": "MONTHLY",
  "status": "ACTIVE",
  "budgetAlert": null
}
```

### Update Budget

```http
PUT /budgets/{id}
```

Request body is the same shape as create budget.

### Delete Budget

```http
DELETE /budgets/{id}
```

## Excel Exports

Export endpoints return binary `.xlsx` bytes, not the standard JSON wrapper.

Response headers:

```http
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="<name>-ddMMyyyy-HHmmss.xlsx"
```

### Transaction Report Export

```http
GET /export/transactions
```

Optional query parameters:

| Parameter | Type |
| --- | --- |
| `walletId` | number |
| `type` | `TransactionType` |
| `status` | `TransactionStatus` |
| `fromDate` | date, `yyyy-MM-dd` |
| `toDate` | date, `yyyy-MM-dd` |

Download filename:

```text
transaction-report-ddMMyyyy-HHmmss.xlsx
```

### Reconciliation Report Export

```http
GET /export/reconciliation/transactions
```

Optional query parameters are the same as transaction export.

Download filename:

```text
reconciliation-report-ddMMyyyy-HHmmss.xlsx
```

The reconciliation report uses this template from the sourcebase:

```text
src/main/resources/templates/reconciliation-report-template.xlsx
```

Current scope: this endpoint exports filtered internal transactions to Excel for manual comparison with invoices, receipts, or bank statements. It does not import statement files or automatically match transactions.

## Status Codes

| Status | Meaning |
| --- | --- |
| `200` | Success |
| `400` | Validation error |
| `401` | Missing, invalid, expired, or logged-out token |
| `403` | Authenticated user does not have the required role |
| `404` | Resource not found, when raised by service logic |

## Quick Test Flow

1. Register user: `POST /auth/register`
2. Login and copy `data.accessToken`
3. Create wallet: `POST /wallets/creation`
4. Create category: `POST /categories/creation`
5. Create transaction: `POST /transactions/creation`
6. List transactions: `GET /transactions/all`
7. Export reconciliation report: `GET /export/reconciliation/transactions?status=POSTED`

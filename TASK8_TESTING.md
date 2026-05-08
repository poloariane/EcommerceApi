# Part 4: Testing & Documentation - Task 8

## Postman Collection

Import this file into Postman:

```text
postman/EcommerceApi-Task8.postman_collection.json
```

Run the Spring Boot app first:

```bash
./gradlew bootRun
```

If the Windows wrapper fails because the project path contains `&`, run:

```powershell
& 'C:\Program Files\Java\jdk-25.0.3\bin\java.exe' -jar '.\gradle\wrapper\gradle-wrapper.jar' bootRun
```

### Covered Flow

| Step | Request | Expected Result |
|------|---------|-----------------|
| 1 | `GET /login` | Login page responds and `XSRF-TOKEN` cookie is captured. |
| 2 | `POST /api/v1/auth/register` | Test user is created with `201 Created`. |
| 3 | `POST /login` | Login succeeds and `JSESSIONID` cookie is set. |
| 4 | `POST /api/v1/orders` | Authenticated order creation succeeds with `201 Created`. |
| 5 | `POST /api/v1/orders` with invalid data | Returns `400 Bad Request` with specific validation errors. |
| 6 | Delete `JSESSIONID`, then `POST /api/v1/orders` | Request fails with `401 Unauthorized`. |

Note: the implemented registration route is `/api/v1/auth/register`, not plain `/register`.

## Browser Console Checks

### Protected Page Redirect

1. Open a browser where you are not logged in, or clear site cookies for `localhost:8080`.
2. Open a protected API URL directly:

```text
http://localhost:8080/api/v1/orders
```

3. In DevTools > Network, confirm the request is blocked because no session is present. Browser navigation may show the login page for page-style requests, while API requests under `/api/**` return `401 Unauthorized`.

### Invalid Form Data Validation

1. Open the checkout page:

```text
http://localhost:8080/Pages/checkout.html
```

2. Leave required checkout fields blank and click **Place Order**.
3. Confirm visible validation messages appear, including:

```text
Full name is required
Country is required
Province is required
Municipality/City is required
Street/Barangay is required
Zip code is required
Please select a payment method
```

4. For backend validation, run the Postman request named **Validation Check - Invalid Order Data** and confirm these messages:

```text
Field 'customerName' Customer name is required
Field 'totalAmount' Total amount must be positive
```

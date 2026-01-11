# Sri Tel - New Customer Experience Solution

This project implements a Microservices-based prototype for "Sri-Care", a scalable customer self-care platform for Sri Tel Ltd. The solution is designed to handle user registration, bill payments, service activation, and notifications.

## Project Structure

The project is a Maven Multi-Module project located in `src/sritel-parent`. It consists of the following microservices:

- **external-system-mocks**: Simulates the legacy Telco Provisioning System and external Payment Gateway.
- **user-service**: Handles customer account registration and authentication.
- **billing-service**: Orchestrates bill payments and service activation requests.
- **notification-service**: Handles asynchronous alerts (Email/SMS) using an event-driven approach.

## Prerequisites

- Java 17 or higher
- Maven 3.x+

## How to Run the Prototype

### 1. Build the Project

Open a terminal, navigate to the project root directory, and build all modules:

```bash
cd src/sritel-parent
mvn clean install
```

### 2. Start the Microservices

You need to run each service in a separate terminal window or tab to simulate the distributed environment.

**Terminal 1: Mock Systems (Provisioning & Payment)**

- **Port:** 8081

```bash
mvn spring-boot:run -pl external-system-mocks
```

**Terminal 2: User Service**

- **Port:** 8082

```bash
mvn spring-boot:run -pl user-service
```

**Terminal 3: Billing Service**

- **Port:** 8083

```bash
mvn spring-boot:run -pl billing-service
```

**Terminal 4: Notification Service**

- **Port:** 8084

```bash
mvn spring-boot:run -pl notification-service
```

## API Usage & Testing

You can use Postman, cURL, or any API client to interact with the services.

### 1. Register User

**Endpoint:** `POST http://localhost:8082/users/register`

**Body (JSON):**

```json
{
  "nic": "123456V",
  "password": "pass",
  "email": "user@sritel.com"
}
```

### 2. Pay Bill

This initiates a payment via the Billing Service, which calls the Payment Mock and then asynchronously triggers a notification.

**Endpoint:** `POST http://localhost:8083/billing/pay`

**Body (JSON):**

```json
{
  "userId": "123456V",
  "amount": 500.0,
  "card": "4111222233334444"
}
```

- **Verification:** Check the **Notification Service** terminal. You should see a log message: `[Notification Service] SENDING ALERT`.

### 3. Activate Service

This simulates activating a Value Added Service (VAS).

**Endpoint:** `POST http://localhost:8083/billing/activate-service`

**Body (JSON):**

```json
{
  "serviceType": "ROAMING",
  "userId": "123456V"
}
```

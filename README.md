# CampusCycle
### A University-Based Second-Hand Commerce Platform for Sustainable Student Living

**Built with** Java · Android · SQLite  
**Team:** Anshika Gupta · Hetanshi Vora · Atlas SkillTech University

---

## Overview

CampusCycle is a university-exclusive second-hand e-commerce Android application. Students can buy and sell pre-owned goods within a verified campus ecosystem using role-based access, SQLite persistence, and a clean layered architecture.

---

## Features

**Buyer**
- Browse and search the marketplace
- View product details and add to cart
- Make offers on products
- Checkout and track order status
- View order history

**Seller**
- Seller command center dashboard
- Add, edit, and delete product listings
- Inventory management
- Accept or decline buyer offers
- Demand-based notifications (auto-triggered on high views / cart adds)

**Auth**
- University email-based registration and login
- Role-based navigation with buyer / seller toggle
- Session persistence via SharedPreferences

---

## App Flow

```mermaid
flowchart TD
    A([User Opens App]) --> B[Login / Register]
    B --> C{Role Selection}
    C -->|Seller| D[SellerDashboardActivity]
    C -->|Buyer| E[MarketplaceActivity]

    D --> F[AddProductActivity]
    D --> G[NotificationsActivity]
    D --> H[InventoryView]

    E --> I[ProductDetailsActivity]
    I --> J[CartActivity]
    J --> K[CheckoutActivity]
    K --> L[OrderHistoryActivity]

    F --> M[ProductService]
    J --> N[OrderService]
    K --> O[PaymentService]

    M --> P[ProductDAO]
    N --> Q[OrderDAO]
    O --> R[CartDAO]
    G --> S[NotificationDAO]
    B --> T[UserDAO]

    P & Q & R & S & T --> U[(SQLite\ncampuscycle.db)]
```

---

## Core User Flows

```mermaid
flowchart LR
    subgraph Buyer["Buyer Flow"]
        B1[Browse Marketplace] --> B2[View Product]
        B2 --> B3[Add to Cart]
        B3 --> B4[Checkout]
        B4 --> B5[Order Confirmed]
        B2 --> B6[Make Offer]
        B6 --> B7[Await Seller Response]
    end

    subgraph Seller["Seller Flow"]
        S1[Dashboard] --> S2[Add Product]
        S2 --> S3[Saved to DB]
        S3 --> S4[Visible in Marketplace]
        S1 --> S5[View Offers]
        S5 --> S6{Accept / Decline}
        S6 -->|Accept| S7[Order Created]
        S1 --> S8[View Notifications]
    end
```

---

## OOP Class Hierarchy

```mermaid
classDiagram
    class User {
        <<abstract>>
        -String userId
        -String name
        -String universityId
        -String email
        -String role
        -boolean isVerified
        +login() void*
        +logout() void*
        +getProfile() String*
    }

    class Seller {
        -boolean isPaymentVerified
        -String paymentAccountId
        +login() void
        +logout() void
        +getProfile() String
    }

    class Buyer {
        +login() void
        +logout() void
        +getProfile() String
    }

    class Product {
        -String productId
        -String title
        -String category
        -double mrp
        -double price
        -String sellerId
        -String status
        -long viewCount
        -long cartCount
        +setPrice(double)
    }

    class Order {
        -String orderId
        -String productId
        -String buyerId
        -String sellerId
        -double amountPaid
        -String orderStatus
    }

    class DemandTracker {
        <<Runnable>>
        +run() synchronized
    }

    class Payable {
        <<interface>>
        +processPayment(double)*
        +verifyAccount()*
    }

    class Notifiable {
        <<interface>>
        +sendNotification(String)*
        +markAsRead(String)*
    }

    class Searchable {
        <<interface>>
        +search(String)*
        +filterByCategory(String)*
    }

    User <|-- Seller
    User <|-- Buyer
    Seller ..|> Payable
    DemandTracker ..|> Runnable
    Notifiable <|.. NotificationService
    Searchable <|.. ProductService
```

---

## Database Schema

```mermaid
erDiagram
    USERS {
        TEXT userId PK
        TEXT name
        TEXT universityId
        TEXT email
        TEXT role
        INTEGER isVerified
    }
    PRODUCTS {
        TEXT productId PK
        TEXT title
        TEXT description
        TEXT category
        REAL mrp
        REAL price
        TEXT sellerId FK
        TEXT status
        INTEGER viewCount
        INTEGER cartCount
        INTEGER timestamp
    }
    ORDERS {
        TEXT orderId PK
        TEXT productId FK
        TEXT buyerId FK
        TEXT sellerId FK
        REAL amountPaid
        TEXT orderStatus
        INTEGER timestamp
    }
    CART {
        TEXT cartId PK
        TEXT buyerId FK
        TEXT productId FK
        INTEGER timestamp
    }
    OFFERS {
        TEXT offerId PK
        TEXT productId FK
        TEXT buyerId FK
        TEXT sellerId FK
        REAL offerAmount
        TEXT status
        INTEGER timestamp
    }
    NOTIFICATIONS {
        TEXT notifId PK
        TEXT targetUserId FK
        TEXT productId FK
        TEXT type
        TEXT message
        INTEGER isRead
        INTEGER timestamp
    }

    USERS ||--o{ PRODUCTS : "sells"
    USERS ||--o{ ORDERS : "places"
    PRODUCTS ||--o{ ORDERS : "ordered in"
    USERS ||--o{ CART : "has"
    PRODUCTS ||--o{ CART : "added to"
    PRODUCTS ||--o{ OFFERS : "receives"
    USERS ||--o{ NOTIFICATIONS : "receives"
```

---

## Project Structure

```
com.javaoops.campuscycle/
│
├── model/
│   ├── User.java               (abstract class)
│   ├── Seller.java             (extends User)
│   ├── Buyer.java              (extends User)
│   ├── Product.java            (price validation in setPrice())
│   ├── Order.java
│   └── Notification.java
│
├── dao/
│   ├── DatabaseHelper.java     (SQLiteOpenHelper)
│   ├── UserDAO.java
│   ├── ProductDAO.java
│   ├── OrderDAO.java
│   ├── CartDAO.java
│   └── NotificationDAO.java
│
├── service/
│   ├── ProductService.java     (implements Searchable)
│   ├── OrderService.java
│   ├── PaymentService.java     (implements Payable)
│   ├── NotificationService.java (implements Notifiable)
│   └── DemandTracker.java      (implements Runnable)
│
├── util/
│   ├── Payable.java            (interface)
│   ├── Notifiable.java         (interface)
│   ├── Searchable.java         (interface)
│   ├── InvalidPriceException.java
│   ├── OutOfStockException.java
│   └── UnverifiedUserException.java
│
└── (root)
    ├── LoginActivity.java
    ├── RegisterActivity.java
    ├── SellerDashboardActivity.java
    ├── AddProductActivity.java
    ├── MarketplaceActivity.java
    ├── CartActivity.java
    ├── NotificationsActivity.java
    └── OrderHistoryActivity.java
```

---

## OOP Concepts

| Concept | Implementation |
|---|---|
| Abstract Class | `User.java` — 3 abstract methods |
| Inheritance | `Seller extends User`, `Buyer extends User` |
| Method Overriding | `login()`, `logout()`, `getProfile()` in both subclasses |
| Method Overloading | `addProduct()` — 2 signatures in `ProductService`; 2 constructors in `Product` |
| Encapsulation | All model classes — private fields + getters/setters |
| Interfaces | `Payable`, `Notifiable`, `Searchable` |
| Polymorphism | `User u = new Seller()` |
| Collections | `ArrayList<Product>`, `ArrayList<Notification>` in DAO returns |
| Exception Handling | `InvalidPriceException`, `OutOfStockException`, `UnverifiedUserException` |
| Threading | `DemandTracker implements Runnable` with `synchronized` block |

---

## Future Scope

- Payment gateway integration
- Real-time chat (post-purchase only)
- Push notifications via Firebase Cloud Messaging
- Cloud database migration
- AI-based price recommendations
- Multi-university expansion

---

*Built for Atlas SkillTech University — Java OOP · Android · SQLite*

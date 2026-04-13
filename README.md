# CampusCycle
### A University-Based Second-Hand Commerce Platform for Sustainable Student Living

**Built with** Java · Android · SQLite  
**Team:** Anshika Gupta · Hetanshi Vora · Atlas SkillTech University

---

## Overview

CampusCycle is a university-exclusive second-hand e-commerce Android application. Every student gets a single unified account — no separate buyer or seller profiles. Any user can list a product, browse the marketplace, make offers, and place orders all from the same account. The platform uses SQLite persistence and a clean layered architecture to demonstrate advanced OOP principles.

---

## Features

**Every user can:**
- Browse and search the marketplace
- View product details
- Add to cart and checkout
- Make offers on products
- Track order status and view order history
- List their own products via the + button
- Manage their personal inventory
- Accept or decline offers on their listings
- Receive demand-based notifications when their product gets high engagement

**Auth**
- University email-based registration and login
- Single unified profile — buy and sell from the same account
- Session persistence via SharedPreferences

---

## App Flow

```mermaid
flowchart TD
    A([User Opens App]) --> B[Login / Register]
    B --> C[Marketplace]

    C --> D[ProductDetailsActivity]
    C --> E[CartActivity]
    C --> F[AddProductActivity\nvia + button]
    C --> G[InventoryActivity]

    D --> E
    E --> H[CheckoutActivity]
    H --> I[OrderHistoryActivity]

    F --> J[ProductService]
    E --> K[OrderService]
    H --> L[PaymentService]

    J --> M[ProductDAO]
    K --> N[OrderDAO]
    L --> O[CartDAO]
    B --> P[UserDAO]

    M & N & O & P --> Q[(SQLite\ncampuscycle.db)]
```

---

## Navigation

```mermaid
flowchart LR
    subgraph BottomNav["Bottom Navigation"]
        N1[Home] 
        N2[Market]
        N3[Inventory]
        N4[Account]
    end

    subgraph Actions["User Actions"]
        A1[Browse Products] 
        A2[Add to Cart]
        A3[Make Offer]
        A4[List Product via +]
        A5[Manage Listings]
        A6[Accept / Decline Offers]
    end

    N2 --> A1
    A1 --> A2
    A1 --> A3
    N2 --> A4
    N3 --> A5
    N3 --> A6
```

---

## Core User Flows

```mermaid
flowchart LR
    subgraph Cart["Cart Flow"]
        B1[Marketplace] --> B2[View Product]
        B2 --> B3[Add to Cart]
        B3 --> B4[Checkout]
        B4 --> B5[Order Confirmed]
    end

    subgraph Offer["Offer Flow"]
        O1[View Product] --> O2[Make Offer]
        O2 --> O3[Offer Stored in DB]
        O3 --> O4[Seller Views Offer]
        O4 --> O5{Accept / Decline}
        O5 -->|Accept| O6[Order Created]
    end

    subgraph Listing["Listing Flow"]
        L1[Tap + Button] --> L2[AddProductActivity]
        L2 --> L3[Validation Applied]
        L3 --> L4[Saved to DB]
        L4 --> L5[Appears in Inventory]
        L5 --> L6[Visible in Marketplace]
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
        -String imageUri
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
        TEXT imageUri
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

    USERS ||--o{ PRODUCTS : "lists"
    USERS ||--o{ ORDERS : "places"
    PRODUCTS ||--o{ ORDERS : "ordered in"
    USERS ||--o{ CART : "has"
    PRODUCTS ||--o{ CART : "added to"
    PRODUCTS ||--o{ OFFERS : "receives"
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
│   └── OfferDAO.java
│
├── service/
│   ├── ProductService.java     (implements Searchable)
│   ├── OrderService.java
│   ├── PaymentService.java     (implements Payable)
│   ├── OfferService.java
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
    ├── MarketplaceActivity.java
    ├── ProductDetailsActivity.java
    ├── AddProductActivity.java
    ├── InventoryActivity.java
    ├── CartActivity.java
    ├── CheckoutActivity.java
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

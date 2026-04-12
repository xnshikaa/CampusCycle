# 🛍️ CampusCycle  
### University-Based Second-Hand E-Commerce Android App  

CampusCycle is a **campus-exclusive marketplace Android application** that enables students to buy and sell items within a trusted university ecosystem.

Built using **Java + Android + SQLite**, the app simulates a real-world e-commerce system including buyer-seller flows, cart management, checkout, and offer negotiation.

---

## 🎯 Problem Statement

Students often face:

- Lack of a trusted platform for buying/selling within campus  
- High costs of new products  
- No structured resale system  
- Informal transactions without tracking  

---

## 💡 Solution

CampusCycle provides:

- Verified university-based marketplace  
- Structured product listing and inventory system  
- Buyer & Seller role separation  
- Cart, checkout, and order tracking  
- Offer negotiation system  

---

## ✨ Key Features

### 👤 Buyer Features
- Browse marketplace  
- View product details  
- Add items to cart  
- Checkout system  
- Track order status (Pending → Confirmed)  
- Make offers on products  

---

### 🧑‍💼 Seller Features
- Seller Dashboard (Command Center)  
- Add / List products manually  
- Inventory management  
- Accept / Decline offers  
- Track listings & activity  

---

## 🔄 Core Functional Flow

```text
Marketplace → Add to Cart → Cart → Checkout → Order Confirmed
Buyer → Make Offer → Seller → Accept / Decline


flowchart TD

A[User - Android App] --> B[UI Layer Activities]

B --> C[Service Layer]
C --> D[DAO Layer]
D --> E[SQLite Database]

E --> F[Users Table]
E --> G[Products Table]
E --> H[Cart Table]
E --> I[Orders Table]
E --> J[Offers Table]

C --> K[OrderService]
C --> L[PaymentService]
C --> M[OfferService]

D --> N[ProductDAO]
D --> O[CartDAO]
D --> P[OrderDAO]
D --> Q[OfferDAO]

🧠 Architecture Explanation
UI Layer → Handles user interaction
Service Layer → Business logic (cart, orders, offers)
DAO Layer → Database operations
Database Layer → SQLite storage
🛠️ Tech Stack
Layer	Technology
Frontend	Android (Java + XML)
Backend	SQLite
Architecture	DAO + Service Layer
IDE	Android Studio
Version Control	Git & GitHub
🗄️ Database Schema
Tables Used
users
products
cart
orders
offers


🎨 UI Highlights
Dark theme premium interface
Card-based layout design
Seller Command Center dashboard
Product detail pages
Mobile-optimized UI

⚙️ Features Implemented
Role-based authentication (Buyer / Seller)
University email validation
Cart system
Checkout system
Order status tracking
Offer negotiation system
Inventory management
Seller dashboard

🚧 Challenges Faced
Cart data not syncing correctly
Marketplace not displaying items initially
UI responsiveness issues
Handling consistent userId across flows
Connecting UI with backend logic

🧠 Solutions Implemented
Fixed DAO-service integration
Ensured consistent user session handling
Improved navigation flow between activities
Refactored UI for mobile responsiveness
Connected all UI buttons to backend logic

🔮 Future Enhancements
Payment gateway integration
Real-time chat system
Push notifications
Cloud database (Firebase / AWS)
AI-based price recommendations

📊 Project Status
Fully functional
End-to-end e-commerce flow implemented
Buyer and Seller systems operational
Ready for demo and evaluation

🎓 Educational Value

This project demonstrates:

Object-Oriented Programming (OOP)
Database integration using SQLite
Android app development
Clean architecture (DAO + Service Layer)
Real-world system design

# Mom’s Kitchen

A JavaFX-based restaurant management and ordering system with role-based workflows (Dietitian, Manager, Customer), DTO-driven client/server communication, and real‑time menu updates.

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
  - [Customer](#customer)
  - [Dietitian](#dietitian)
  - [Manager](#manager)
  - [Real‑time Updates](#real-time-updates)
- [Architecture](#architecture)
- [Data Model (DTOs)](#data-model-dtos)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
  - [Database Schema (example)](#database-schema-example)
  - [Build & Run](#build--run)
- [Key Workflows](#key-workflows)
  - [Dietitian → Change Request](#dietitian--change-request)
  - [Manager → Approve Request](#manager--approve-request)
  - [Customer → Checkout & Refund Policy](#customer--checkout--refund-policy)
- [Events & Payloads](#events--payloads)
---

## Overview
Mom’s Kitchen is a multi‑role restaurant/order system built with JavaFX (client) and a Java server. The app uses Data Transfer Objects (DTOs) on the client to decouple UI concerns from persistence models. Menu updates follow a request/approve workflow between Dietitians and Managers. After approval, the server broadcasts updates to all connected clients so menus stay in sync in real time.

**Highlights**
- Per‑restaurant menu filtering with an `"all"` chain‑wide scope.
- Predefined, per‑dish preferences (e.g., *extra cheese*, *no tomato*).
- Dietitian submits change requests (price/ingredients/restaurant scope) → Manager approves.
- Customer shopping cart, delivery details, and order persistence.
- Order lookup by customer ID with a simple, time‑based refund policy.


## Features

### Customer
- Browse menu by **selected restaurant**; support for chain‑wide dishes (`restaurants` includes `"all"`).
- Dish‑level **preferences** (e.g., toggles/choices like *extra cheese*, *no tomato*).
- **Cart** with quantity and preferences per item.
- **Checkout** flow (`fillDetailsView`) collects name, ID, address, desired delivery time, and credit card.
- Order is **saved to SQL**; user returns to the home screen with confirmation.
- **View Orders** by ID; **Cancellation policy**:
  - ≥ **3 hours** before delivery: **full refund**
  - **1–3 hours**: **50% refund**
  - < **1 hour**: **no refund**

### Dietitian
- Double‑click a dish to open its **details** view.
- **Add** new dishes.
- **Update** ingredients.
- Mark a dish as **chain‑wide** (applies to all restaurants) or specify restaurant list.
- **Request** price changes or restaurant‑list updates (stored as a change request; does not immediately mutate the dish).

### Manager
- Review & **approve** change requests in `ApproveChangesView.fxml`.
- Backed by `RequestedChangesDTO` to standardize decisions and auditability.
- Upon approval, the **server applies** the change to the canonical dish and **broadcasts** the update to all clients.

### Real‑time Updates
- The server (`MomServer.java`) broadcasts **menu/dish change events** to all connected clients.
- Clients **listen** for events and update local state/UI accordingly (additions, price changes, ingredient updates, and restaurant scope changes).


## Architecture
- **Client**: JavaFX application (FXML views + controllers). Uses DTOs to render menus, manage cart state, and submit requests.
  - Controllers include (illustrative): `menuController` (all dishes), `orderFoodController` (dishes for selected restaurant), `approveChangesController` (manager view), `fillDetailsView` (checkout form).
  - Core client types: `dishDTO`, `RequestedChangesDTO`, `CartItem`.
- **Server**: Java backend (e.g., a lightweight socket or HTTP server) that:
  1. Persists entities to SQL.
  2. Accepts Dietitian requests and records them as *pending*.
  3. Applies Manager approvals to canonical records.
  4. Broadcasts change events to all connected clients.
- **Persistence**: SQL database (engine agnostic; see example schema below).

> Note: Exact package names and build system may vary. Adjust paths and commands to match your repository layout.


## Data Model (DTOs)

### `dishDTO`
```json
{
  "id": 101,
  "name": "Margherita Pizza",
  "description": "Tomato, mozzarella, basil",
  "basePrice": 42.0,
  "preferences": ["extra cheese", "no tomato"],
  "restaurants": ["Nazareth", "Haifa", "all"]
}
```

### `RequestedChangesDTO`
```json
{
  "id": 501,
  "dishId": 101,
  "type": "PRICE" , // or INGREDIENTS, RESTAURANTS
  "proposedValue": "45.0", // new price OR ingredient diff OR CSV of restaurants
  "reason": "Seasonal dairy costs",
  "requestedBy": "dietitian@momskitchen",
  "requestedAt": "2025-03-25T11:32:10Z",
  "status": "PENDING" // PENDING | APPROVED | REJECTED
}
```

### `CartItem` (client‑side)
```json
{
  "dishId": 101,
  "name": "Margherita Pizza",
  "selectedPreferences": ["extra cheese"],
  "quantity": 2,
  "unitPrice": 42.0
}
```

### `Order`
```json
{
  "orderId": "ORD-20250330-000123",
  "customerId": "123456789",
  "customerName": "Rami",
  "address": "123 Olive St, Nazareth",
  "deliveryTime": "2025-04-01T18:30:00+02:00",
  "items": [ {"dishId": 101, "qty": 2, "prefs": ["extra cheese"], "unitPrice": 42.0} ],
  "subtotal": 84.0,
  "refundPolicy": ">=3h:100%, 1–3h:50%, <1h:0%",
  "status": "PLACED"
}
```


## Project Structure
```
MomKitchens/
├─ client/
│  ├─ src/main/java/
│  │  ├─ controllers/
│  │  ├─ dto/
│  │  ├─ models/
│  │  └─ Main.java
│  ├─ src/main/resources/
│  │  ├─ fxml/
│  │  │  ├─ menu.fxml
│  │  │  ├─ orderFood.fxml
│  │  │  ├─ ApproveChangesView.fxml
│  │  │  └─ fillDetailsView.fxml
│  │  └─ application.properties
├─ server/
│  ├─ src/main/java/
│  │  ├─ MomServer.java
│  │  ├─ repository/
│  │  ├─ service/
│  │  └─ events/
│  └─ src/main/resources/
│     └─ application.properties
└─ README.md
```


## Getting Started

### Prerequisites
- **JDK** 17 or newer
- **JavaFX** SDK 17+ (if not using a build plugin that bundles JavaFX)
- **SQL database** (e.g., SQLite/MySQL/PostgreSQL)
- **Build tool**: Maven or Gradle (adjust commands accordingly)

### Configuration
Configure DB connectivity and server settings via properties or environment variables. Example:

`client/src/main/resources/application.properties`
```
server.host=localhost
server.port=9090
```

`server/src/main/resources/application.properties`
```
db.url=jdbc:sqlite:./momskitchen.db
# For MySQL/PostgreSQL:
# db.url=jdbc:mysql://localhost:3306/momskitchen
# db.user=...
# db.pass=...
server.port=9090
```

### Database Schema (example)
```sql
CREATE TABLE dishes (
  id INTEGER PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT,
  base_price REAL NOT NULL,
  preferences TEXT,         -- JSON array or CSV
  restaurants TEXT NOT NULL -- JSON array or CSV; may include "all"
);

CREATE TABLE requested_changes (
  id INTEGER PRIMARY KEY,
  dish_id INTEGER NOT NULL,
  type TEXT NOT NULL,           -- PRICE | INGREDIENTS | RESTAURANTS
  proposed_value TEXT NOT NULL,  -- numeric/string/CSV
  reason TEXT,
  requested_by TEXT,
  requested_at TEXT,
  status TEXT NOT NULL DEFAULT 'PENDING',
  FOREIGN KEY(dish_id) REFERENCES dishes(id)
);

CREATE TABLE orders (
  id TEXT PRIMARY KEY,
  customer_id TEXT NOT NULL,
  customer_name TEXT,
  address TEXT,
  delivery_time TEXT NOT NULL,
  subtotal REAL NOT NULL,
  status TEXT NOT NULL
);

CREATE TABLE order_items (
  id INTEGER PRIMARY KEY,
  order_id TEXT NOT NULL,
  dish_id INTEGER NOT NULL,
  quantity INTEGER NOT NULL,
  unit_price REAL NOT NULL,
  selected_prefs TEXT, -- JSON array or CSV
  FOREIGN KEY(order_id) REFERENCES orders(id),
  FOREIGN KEY(dish_id) REFERENCES dishes(id)
);
```

### Build & Run
**Maven (example):**
```bash
# From project root
mvn -f server/pom.xml clean package
java -jar server/target/server-*.jar

mvn -f client/pom.xml clean javafx:run
```

**Gradle (example):**
```bash
# From project root
./gradlew :server:build
java -jar server/build/libs/server-*.jar

./gradlew :client:run
```

> Ensure your build config includes the JavaFX modules required by your platform (controls, fxml, graphics, etc.).


## Key Workflows

### Dietitian → Change Request
1. Dietitian opens a dish details view.
2. Chooses a change type (**PRICE/INGREDIENTS/RESTAURANTS**).
3. Enters a proposed value and reason → **Submit Request**.
4. Server saves the request as **PENDING**.

### Manager → Approve Request
1. Manager opens **ApproveChangesView** and reviews the pending list.
2. For each request: **Approve** or **Reject**.
3. On approval, server mutates the canonical dish (e.g., set new price, update ingredients, or replace restaurant list) and **broadcasts** an event.
4. All connected clients receive the event and update their UI/state.

### Customer → Checkout & Refund Policy
1. Customer adds dishes (with preferences) to the cart.
2. Proceeds to **Checkout** and fills in personal + delivery details.
3. Places order; server persists the order and returns an order ID.
4. Customer can query **View Orders** by ID.
5. **Refunds** if canceled:
   - ≥ 3 hours before delivery: 100%
   - 1–3 hours: 50%
   - < 1 hour: 0%


## Events & Payloads
> Actual transport (e.g., sockets/WebSocket/HTTP SSE) is implementation‑specific. Below are canonical payloads the client expects.

**`dish.added`**
```json
{
  "event": "dish.added",
  "dish": { /* dishDTO */ }
}
```

**`dish.updated`** (price/ingredients)
```json
{
  "event": "dish.updated",
  "dishId": 101,
  "fields": { "basePrice": 45.0, "ingredientsDiff": "+ buffalo mozzarella" }
}
```

**`dish.scope.changed`** (restaurant list)
```json
{
  "event": "dish.scope.changed",
  "dishId": 101,
  "restaurants": ["Nazareth", "Haifa", "Tiberias"]
}
```

**`request.status`** (manager decision)
```json
{
  "event": "request.status",
  "requestId": 501,
  "status": "APPROVED",
  "appliedAt": "2025-03-25T12:05:00Z"
}
```


## Testing
- **Unit**: DTO conversions, restaurant filtering logic (including `"all"`), refund calculation by (now − deliveryTime).
- **Integration**: Request → Approve → Broadcast → Client refresh pipeline.
- **UI/Manual**: Add dish, request change, approve, observe client updates across two running clients.

**Sample test matrix**
- Add dish with preferences; verify render + selection persists to order.
- Change price via request/approve; verify updated price propagates.
- Change restaurant scope; verify filtering updates in `orderFoodController`.
- Refund policy at boundaries: exactly 3h, exactly 1h, off‑by‑one minute.
- Offline client reconnect → receives missed updates (if supported) or full refresh.





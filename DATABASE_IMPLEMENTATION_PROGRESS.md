# Database Implementation Progress

This document tracks the progress of implementing the local Room database and future Firebase integration.

## Overall Plan

*   [X] **Phase 1: Core Entities Setup (Product, Customer, Supplier) with Room** - *COMPLETED*
    *   [X] Stage 1.1: Add Room Dependencies & Initial Progress File
    *   [X] Stage 1.2: Define `ProductEntity` & `ProductDao` - *Completed*
    *   [X] Stage 1.3: Define `CustomerEntity` & `CustomerDao` - *Completed*
    *   [X] Stage 1.4: Define `SupplierEntity` & `SupplierDao` - *Completed*
    *   [X] Stage 1.5: Create `AppDatabase` class - *Completed*
    *   [X] Stage 1.6: Implement basic Repository for Product, Customer, Supplier. - *Completed*
    *   [X] Stage 1.7: Initial integration for creating/reading entities. - *Completed*
*   [X] **Phase 2: Order Management Entities (Room)** - *COMPLETED*
    *   [X] Stage 2.1: Define `OrderEntity` & `OrderDao` - *Completed*
    *   [X] Stage 2.2: Define `OrderItemEntity` & `OrderItemDao` - *Completed*
    *   [X] Stage 2.3: Update `AppDatabase` - *Completed*
    *   [X] Stage 2.4: Extend Repository for Orders. - *Completed*
    *   [X] Stage 2.5: Integrate Order creation/viewing. - *Completed*
*   **Phase 3: User Preferences (Room)**
    *   [X] Stage 3.1: Define `UserPreferenceEntity` & `UserPreferenceDao` - *Completed*
    *   [X] Stage 3.2: Update `AppDatabase` - *Completed*
    *   [X] Stage 3.3: Extend Repository for User Preferences. - *Completed*
    *   [X] Stage 3.4: Integrate preference loading/saving. - *Completed*
*   [X] **Phase 3: User Preferences (Room)** - *COMPLETED*
    *   (This line is a summary, actual list of stages for phase 3 is above)
*   **Phase 4: Warehouse & Basic Stock Entities (Room)** - *In Progress*
    *   [X] Stage 4.1: Update Progress File for Phase 4 Start - *Completed*
    *   [X] Stage 4.2: Define `WarehouseEntity` & `WarehouseDao` - *Completed*
    *   [X] Stage 4.3: (Re-evaluation) Product Stock and Warehouse Linking - *Completed*
    *   [X] Stage 4.4: Update `AppDatabase` - *Completed*
    *   [X] Stage 4.5: Extend Repository for Warehouses - *Completed*
    *   [X] Stage 4.6: Initial Integration for Warehouses (Placeholder) - *Completed*
    *   [X] Stage 4.7: Update Progress File & Submit - *Completed*
*   **Phase 4: Warehouse & Basic Stock Entities (Room)** - *In Progress (Completed Part A: WarehouseEntity)*
    *   **Phase 4a: Warehouse Entity** (Already summarized as completed by Stage 4.7)
    *   **Phase 4b: Stock-Warehouse Linking** - *COMPLETED*
        *   [X] Stage 4b.1: Update Progress File for Phase 4b Start - *Completed*
        *   [X] Stage 4b.2: Define `StockAtWarehouseEntity` & `StockAtWarehouseDao` - *Completed*
        *   [X] Stage 4b.3: Update `AppDatabase` - *Completed*
        *   [X] Stage 4b.4: Extend Repository for StockAtWarehouse - *Completed*
        *   [X] Stage 4b.5: Initial Integration for StockAtWarehouse (Placeholder) - *Completed*
        *   [X] Stage 4b.6: Update Progress File & Submit - *Completed*
*   [X] **Phase 4: Warehouse & Basic Stock Entities (Room)** - *COMPLETED*
*   **Phase 5: Advanced Room Features & Refinements** - *In Progress*
    *   **Phase 5a: Implement Room Database Migrations** - *COMPLETED*
        *   [X] Stage 5a.1: Update Progress File for Phase 5a Start - *Completed*
        *   [X] Stage 5a.2: Enable Schema Export & Define Location - *Completed*
        *   [X] Stage 5a.3: Plan and Implement a Schema Change & Migration - *Completed*
        *   [X] Stage 5a.4: Add Migration to `AppDatabase` Builder - *Completed*
        *   [X] Stage 5a.5: Test Migration (Conceptual & via DebugViewModel) - *Completed*
        *   [X] Stage 5a.6: Update Progress File & Submit - *Completed*
    *   **Phase 5b: Implement Complex Queries & Relationships** - *COMPLETED*
        *   [X] Stage 5b.1: Update Progress File for Phase 5b Start - *Completed*
        *   [X] Stage 5b.2: Implement Product Search Query - *Completed*
        *   [X] Stage 5b.3: Implement Order Date Range Query - *Completed*
        *   [X] Stage 5b.4: Refactor `insertOrderWithItems` to use `@Transaction` (via `AppDatabase.runInTransaction`) - *Completed*
        *   [X] Stage 5b.5: Test New Queries & Transactional Method - *Completed*
        *   [X] Stage 5b.6: Update Progress File & Submit - *Completed*
*   [X] **Phase 5: Advanced Room Features & Refinements** - *COMPLETED*
    *   (This line is a summary, actual list of stages for phase 5 is above)
*   **Phase 6: Firebase Integration - Firestore (Parallel or Sequential)** - *In Progress*
    *   **Phase 6a: Initial Firebase Setup & Product Sync Test** - *COMPLETED*
        *   [X] Stage 6a.1: Update Progress File & Review Firebase Project Setup - *Completed*
        *   [X] Stage 6a.2: Verify `google-services.json` & Add Firebase SDKs - *Completed*
        *   [X] Stage 6a.3: Implement One-Way Sync for `ProductEntity` (Room -> Firestore) - *Completed*
        *   [X] Stage 6a.4: Basic Firestore Read Operation (Firestore -> App) - *Completed*
        *   [X] Stage 6a.5: Update Progress File & Submit - *Completed*
    *   **Pending Firestore Sync Tasks (Future Sub-Phases of Phase 6):**
        *   Sync other entities (Customers, Orders, etc.) with Firestore.
        *   Implement bi-directional sync strategies and conflict resolution.
    *   (Further sub-phases for other entities and bi-directional sync to be defined)
*   **Phase 7: Firebase Integration - Authentication** - *In Progress*
    *   [X] Stage 7.1: Update Progress File for Phase 7 Start - *Completed*
        *   [X] Stage 7.2: Add Firebase Authentication SDK - *Completed*
        *   [X] Stage 7.3: Basic Email/Password Sign-Up UI (Placeholder) - *Completed (Existing UI sufficient)*
        *   [X] Stage 7.4: Implement Email/Password Sign-Up Logic - *Completed*
        *   [X] Stage 7.5: Implement Email/Password Sign-In Logic - *Completed*
        *   [X] Stage 7.6: Implement Sign-Out Logic - *Completed*
    *   [ ] Stage 7.7: Observe Auth State Changes
    *   [ ] Stage 7.8: Update Firestore Security Rules (Conceptual)
    *   [ ] Stage 7.9: Test Authentication Flow (Conceptual & via UI)
    *   [ ] Stage 7.10: Update Progress File & Submit
*   **Phase 8: Firebase Integration - Cloud Storage (Optional, for images etc.)**
    *   (Details to be defined)
*   **Phase 9: Warehouse UI** - *COMPLETED*
    *   [X] Stage 9.1: Create `WarehouseViewModel`
    *   [X] Stage 9.2: Move `OrderDetailScreen` to warehouse package
    *   [X] Stage 9.3: Fix `TODO()`s in `OrderDetailScreen`
    *   [X] Stage 9.4: Create `PickListViewModel`
    *   [X] Stage 9.5: Implement warehouse logic in `WarehouseViewModel`
    *   [X] Stage 9.6: Implement picking logic in `PickListViewModel`
*   **Phase 10: Review and Refactor** - *In Progress*
    *   [X] Stage 10.1: Review implementation
    *   [ ] Stage 10.2: Refactor and fix issues
*   **Developer Notes**
    *   Do not remove the Toast messages. They are used for verification purposes.
*   **Schema Versioning and Backup**
    *   The database schema version is managed by the `version` parameter in the `@Database` annotation in the `AppDatabase` class.
    *   When the schema is updated, a new migration must be created and added to the `AppDatabase` builder.
    *   Before applying a major schema change, it is recommended to back up the local data to a file. This can be done by copying the database file from the device to a safe location.
*   **Phase 11: Error and State Management** - *COMPLETED*
    *   [X] Stage 11.1: Create `Resource<T>` class
    *   [X] Stage 11.2: Refactor ViewModels and Repository to use `Resource<T>`
*   **Phase 12: Two-Way Sync with Firestore** - *COMPLETED*
    *   [X] Stage 12.1: Add `lastUpdated` field to `LocationEntity` and `ProductLocationEntity`
    *   [X] Stage 12.2: Create migration to add `lastUpdated` column
    *   [X] Stage 12.3: Implement two-way sync in `AppRepositoryImpl`
*   **Phase 13: Firestore Security Rules** - *COMPLETED*
    *   [X] Stage 13.1: Create `firestore.rules` file
*   **Phase 14: Session Persistence (Auth)** - *COMPLETED*
    *   [X] Stage 14.1: Add session persistence documentation to `README.md`
*   **Phase 15: Offline-first or connectivity strategy** - *COMPLETED*
    *   [X] Stage 15.1: Refactor `AuthRepositoryImpl` to use local database as single source of truth when offline
*   **Phase 16: Accessibility** - *COMPLETED*
    *   [X] Stage 16.1: Review contrasts and sizes for accessibility



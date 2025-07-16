# Firestore Data Models for Warehouse System

This document outlines the data structure for the collections used by the warehouse and inventory management system in Firestore.

## 1. `locations` Collection

This collection stores the primary locations where inventory can be held (e.g., "Main Store", "Warehouse A").

-   **Path:** `/locations/{locationId}`
-   **`locationId`**: The unique ID of the location, matching the `locationId` from the local Room database.

### Document Fields:

| Field       | Type     | Description                                               |
| :---------- | :------- | :-------------------------------------------------------- |
| `locationId`  | `string` | The unique identifier for the location.                   |
| `name`        | `string` | The user-friendly name of the location (e.g., "Warehouse"). |
| `address`     | `string` | The physical address of the location. (Nullable)          |
| `capacity`    | `number` | The storage capacity of the location. (Nullable)          |
| `notes`       | `string` | Any additional notes about the location. (Nullable)       |

## 2. `product_locations` Collection

This collection tracks the quantity of each product at every specific spot within a location. This denormalized structure allows for efficient querying.

-   **Path:** `/product_locations/{productLocationId}`
-   **`productLocationId`**: The unique ID for this specific stock entry, matching the `productLocationId` from Room.

### Document Fields:

| Field               | Type     | Description                                                                 |
| :------------------ | :------- | :-------------------------------------------------------------------------- |
| `productLocationId`   | `string` | The unique identifier for this stock record.                                |
| `productId`           | `string` | The ID of the product. Used for querying all locations for a product.     |
| `locationId`          | `string` | The ID of the location where the product is stored. Used for querying all products at a location. |
| `quantity`            | `number` | The quantity of the product at this specific spot.                          |
| `aisle`               | `string` | The aisle identifier. (Nullable)                                            |
| `shelf`               | `string` | The shelf identifier. (Nullable)                                            |
| `level`               | `string` | The level or bin identifier. (Nullable)                                     |

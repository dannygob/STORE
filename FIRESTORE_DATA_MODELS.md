# Firestore Data Models

This document outlines the data models used in Firestore for the warehouse and inventory management system.

## Locations

The `locations` collection stores information about physical locations within the warehouse.

**Collection:** `locations`

**Document ID:** `locationId` (String)

**Fields:**

*   `name`: (String) The name of the location (e.g., "Aisle 1, Shelf A").
*   `barcode`: (String) The barcode of the location.

## Product Locations

The `product_locations` collection stores information about the stock of each product at a specific location.

**Collection:** `product_locations`

**Document ID:** Automatically generated

**Fields:**

*   `productId`: (String) The ID of the product.
*   `locationId`: (String) The ID of the location.
*   `quantity`: (Number) The quantity of the product at the location.
*   `lastUpdated`: (Timestamp) The timestamp of the last update.

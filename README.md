# E-commerce API - Product Catalog

## Overview
RESTful API for managing products using Spring Boot with in-memory storage.

## Setup Instructions
1. Prerequisites: Java 25+
2. Run `./gradlew bootRun`
3. Access API at `http://localhost:8080/api/v1/products`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/products` | Get all products |
| GET | `/api/v1/products/{id}` | Get product by ID |
| POST | `/api/v1/products` | Create product |
| PUT | `/api/v1/products/{id}` | Full update |
| PATCH | `/api/v1/products/{id}` | Partial update |
| DELETE | `/api/v1/products/{id}` | Delete product |

## Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 404 | Not Found |

## Authors
- Abundo, Clarissa Mae T.
- Polo, Ariane C.
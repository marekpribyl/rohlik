Requirements
============

Write a RESTful application that maintains a database of products and orders, ensuring accuracy, consistency, and scalability while demonstrating ownership, creativity, and efficiency in your solution.


## Order Operations

* Create an order – Orders can contain one or multiple products in specific quantities.
* Cancel an order – Canceling an order should release reserved stock.
* Pay for an order – Marks the order as paid.

## Product Operations

* Create a product – Add a new product to the catalog.
* Delete a product – Remove a product (if no active orders depend on it).
* Update a product – Modify product details such as name, price, or stock quantity.
* List products - List all products

## Business Rules & Constraints

* Every product must have a name, quantity in stock, and price per unit.
* Orders decrease stock availability immediately upon creation.
* If an order exceeds stock availability, return an error response with missing product details.
* Orders reserve stock for 30 minutes. If unpaid within that period, the order becomes invalid, and stock is released.
* Canceled orders immediately release the reserved stock.

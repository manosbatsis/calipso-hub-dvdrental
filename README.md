# calipso-hub-dvdrental
Calipso-hub tutorial/example project


## Sample Workflow

Sample workflow of an application client used by clerk on the store counter. The client may have to hold references of objects in memory as indicated in the "notes" since the services API is totally stateless. The workflow has three simple steps:

- Scan Client Card
- Scan DVD boxes
- Submit Payment

The steps are explained in detail within the following sections.

### Scan Client Card

Scan the 2D barcode of the client card to retrieve the corresponding user:

Method | URL
-------|--------
GET | http://localhost:8080/calipso/api/rest/filmInventoryEntries/:userId

Notes

- The API client is able to authenticate each request (required role: staff or admin)
- The API client must store the user until the end of the workflow (either payment or abort).

### Scan DVD boxes

Scan the 2D barcode of each physical copy and use the utility "orders" endpoint to get an Order object that includes the appropriate cost for both new and returned rentals

Method | URL
-------|--------
GET | http://localhost:8080/calipso/api/rest/order/orders/:filmInventoryEntryId?days

The <code>days</code> can be used to control the cost for new rentals. The clients state the number of days they intent to rent the inventory entry for and the service calculates the cost according to the pricing strategy of the film. The days parameter is ignored for returns, in which case the current date is used to calculate any amount due for late returns.

Name        | Type    | Description
------------|---------|------------
inventoryId | string  | The UUID of the inventory entry
filmTitle   | string  | The film title
filmMpaa    | string  | The film MPAA rating (unrated, G, PG, PG-13, R, NC-17)
rentalId    | string  | The UUID of the current inventory rental or null if new (i.e. if the inventory entry is available)
cost        | float   | The initial or pending cost for the inventory entry rental
days        | int     | The number of days included in the cost
pricingStrategy | object | The pricinig strategy, see [Film Pricing Strategies](#film_pricing_strategies)

Notes
- The API client is able to authenticate each request (required role: staff or admin)
- The API client can store the entry for use in the next step, update with a new request to adjust the days or discard it at any time (i.e. if the store client changed his mind)

### Submit Payment

Once ready, the staff can submit the set of orders for payment by submitting a POST request, using an Orders object as the JSON body.


Method | URL
-------|--------
POST | http://localhost:8080/calipso/api/rest/order/orders

The returning Payments JSON has the following properties:

Name        | Type        | Description
------------|-------------|------------
items       | collection  | The persisted payments (one per inventory rental/return)
totalCost   | float       | he total cost of enclosed payments


## API Reference

The following sections document parts of the services API. The application exposes more endpoints for auth and SCRUD; those are "inherited" by the calipso-hub-webapp [WAR overlay](http://maven.apache.org/plugins/maven-war-plugin/overlays.html).

### DVD Inventory

The DVD inventory endpoints refer to the actual physical DVD boxes available for rent by the store.

Action | Method | URL | Request body
-------|--------|-----|-----------------
Create | POST |  http://localhost:8080/calipso/api/rest/filmInventoryEntries | JSON
Update | PUT |  http://localhost:8080/calipso/api/rest/filmInventoryEntries | JSON
Search  | GET |  http://localhost:8080/calipso/api/rest/filmInventoryEntries?:paramN=valueN | ignored

Search parameters:

Name | Type
-----|------
id | UUID string
film | UUID string
createdDate | timestamp
lastModifiedBy | UUID string
lastModifiedDate | timestamp


### Films

The film endpoints refer to films in the general sense of audio/visual production titles.

Action | Method | URL | Request body
-------|--------|-----|-----------------
Create | POST |  http://localhost:8080/calipso/api/rest/films | JSON
Update | PUT |  http://localhost:8080/calipso/api/rest/films | JSON
Search  | GET |  http://localhost:8080/calipso/api/rest/films?:paramN=valueN | ignored

Search parameters:

Name | Type
-----|------
id | UUID string
title | string (‘%’ is a “like” wildcard)
description | string (‘%’ is a “like” wildcard)
mpaaRating | enum, one of: unrated, G, PG, PG-13, R, NC-17
createdBy | UUID string
createdDate | timestamp
lastModifiedBy | UUID string
lastModifiedDate | timestamp

### Film Actors

Information on physical persons with roles in films on record

Action | Method | URL | Request body
-------|--------|-----|-----------------
Create | POST |  http://localhost:8080/calipso/api/rest/filmActors | JSON
Update | PUT |  http://localhost:8080/calipso/api/rest/filmActors | JSON
Search  | GET |  http://localhost:8080/calipso/api/rest/filmActors?:paramN=valueN | ignored

Search parameters:

Name | Type
-----|------
id | UUID string
firstName | string (‘%’ is a “like” wildcard)
lastName | string (‘%’ is a “like” wildcard)
createdBy | UUID string
createdDate | timestamp
lastModifiedBy | UUID string
lastModifiedDate | timestamp


### Film Categories

Film categories (action, drama etc.).

Action | Method | URL | Request body
-------|--------|-----|-----------------
Create | POST |  http://localhost:8080/calipso/api/rest/filmCategories | JSON
Update | PUT |  http://localhost:8080/calipso/api/rest/filmCategories | JSON
Search  | GET |  http://localhost:8080/calipso/api/rest/filmCategories?:paramN=valueN | ignored

Search parameters:

Name | Type
-----|------
id | UUID string
name | string (‘%’ is a “like” wildcard)
parent | UUID string
createdBy | UUID string
createdDate | timestamp
lastModifiedBy | UUID string
lastModifiedDate | timestamp


### Film Pricing Strategies


The store has three types of films.

<ul>
<li>New releases – Price is <premium price> times number of days rented.</li>
<li>Regular films – Price is <basic price> for the fist 3 days and then
<basic price> times the number of days over 3.</li>
<li>Old film - Price is <basic price> for the fist 5 days and then <basic
price> times the number of days over 5</li>
</ul>

This is modeled in three properties:

<dl>
  <dt><code>initialPrice</code></dt>
  <dd>the price to rent a film</dd>
  <dt><code>daysFree</code></dt>
  <dd>the number of days included in the <code>initialPrice</code></dd>
  <dt><code>dailyPrice</code></dt>
  <dd>the price per day (not counting <code>daysFree</code>)</dd>
</dl>

Action | Method | URL | Request body
-------|--------|-----|-----------------
Create | POST |  http://localhost:8080/calipso/api/rest/filmPricingStrategies | JSON
Update | PUT |  http://localhost:8080/calipso/api/rest/filmPricingStrategies | JSON
Search  | GET |  http://localhost:8080/calipso/api/rest/filmPricingStrategies?:paramN=valueN | ignored

Search parameters:

Name | Type
-----|------
id | UUID string
initialPrice | float
daysFree | int
dailyPrice | float
createdBy | UUID string
createdDate | timestamp
lastModifiedBy | UUID string
lastModifiedDate | timestamp


### Rentals

Inventory rentals

Action | Method | URL | Request body
-------|--------|-----|-----------------
Create | POST |  http://localhost:8080/calipso/api/rest/rentals | JSON
Update | PUT |  http://localhost:8080/calipso/api/rest/rentals | JSON
Search  | GET |  http://localhost:8080/calipso/api/rest/rentals?:paramN=valueN | ignored

Search parameters:

Name | Type
-----|------
id | UUID string
customer | UUID string
inventory | UUID string
createdBy | UUID string
rentalDate | timestamp
returnDate | timestamp
createdDate | timestamp
lastModifiedBy | UUID string
lastModifiedDate | timestamp

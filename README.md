# calipso-hub-dvdrental

This project demonstrates how to use  [Calipso-Hub](https://github.com/abissgr/calipso-hub) to build a DVD Rental Store app with minimal effort.


- [Architecture and Current Status](#architecture-and-current-status)
	- [Front-end](#front-end)
	- [Back-end](#back-end)
	- [Tests](#tests)
- [Build and Run](#build-and-run)
- [Sample Workflow](#sample-workflow)
	- [Scan Client Card](#scan-client-card)
	- [Scan DVD boxes](#scan-dvd-boxes)
	- [Submit Payment](#submit-payment)
- [API Reference](#api-reference)
	- [Orders](#orders)
	- [DVD Inventory](#dvd-inventory)
	- [Films](#films)
	- [Film Actors](#film-actors)
	- [Film Categories](#film-categories)
	- [Film Pricing Strategies](#film-pricing-strategies)
	- [Rentals](#rentals)


### Architecture and Current Status

#### Front-end

The front end is based on backbone marionette and provided by the calipso overlay WAR. Backbone models are the only code pending to provide an administrative SCRUD UI (thanks to calipso's use-case driven front-end), unless those models [become optional first](https://github.com/abissgr/calipso-hub/issues/26).

#### Back-end

Most SCRUD endpoints [documented bellow](#api_reference) use Controller/Service/Repository classes generated automatically by calipso on startup using javassist. The small amounts of business logic and custom methods can be found in the few custom components in the code. The larger part of the code is simple JPA entity models.

The only manually created endpoint is that of [Orders](#orders)

#### Tests

New rentals and returns are tested on startup by [code in application initializer](https://github.com/manosbatsis/calipso-hub-dvdrental/blob/master/src/main/java/gr/abiss/calipso/DvdRentalAppInitializer.java#L172) as shown bellow but this needs to be refactored to integration tests run in-conainer during  POM's integration profile (see also [#1](https://github.com/manosbatsis/calipso-hub-dvdrental/issues/1)).

![console output of DvdRentalAppInitializer](https://raw.githubusercontent.com/manosbatsis/calipso-hub-dvdrental/master/etc/img/appinit_tests.png)

## Build and Run

a) Clone the repository
b) Copy HOWTO.txt to dev.properties
c) mvn build install jetty:run

If you want to use the integration build profile to run tests or optimize client-side code you need to have [node](https://nodejs.org) installed.

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

With the exception of [Orders](#orders),  endpoints are based on code tiers (Controller, Service, Repository) calipso generated using javassist during application initialization.


### Orders

Orders endpoints use DTOs and manually written code to provide the convenient API used in the [Sample Workflow](#sample_workflow).

Action          | Method | URL | Request JSON body  | Response JSON body
----------------|--------|-----|--------------------|--------------------
Calculate order | GET    | http://localhost:8080/calipso/api/rest/order/orders/:filmInventoryEntryId?days | none | Order DTO
Finalize orders | POST   | http://localhost:8080/calipso/api/rest/order/orders | Orders DTO  | Payments DTO

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

# calipso-hub-dvdrental
Calipso-hub tutorial/example project


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

## Sample Workflow

Scan the 2D barcode of physical copy and load the corresponding inventory entry:

GET http://localhost:8080/calipso/api/rest/filmInventoryEntries/:id

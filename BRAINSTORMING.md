## Training Parts

### Introduction into Commercetools Platform a.k.a. SPHERE.IO

* HTTP(S) API with JSON
* show MC + curl authentication and example call

### Hello SDK

* GitHub Repo
* issue tracker
* modules (different HTTP clients)
* Javadoc
    * Release Notes
    * Overview of Endpoints
* Architecture
    * methods of SphereClient
    * async stuff/blocking client
        * requires Java 8 CompletionStage
    * SphereRequest
        * show manually implementation with http request intent
        * get as byte[]
        * get as JSON
    * initialize client
    * test examples with blocking client
    * references and reference expansion

### CRUD
* immutable objects, of-method, withers
* CreateCommands
* GetByXyz
* Query
    * requires Java 8 lambdas
    * predicates
    * sort
    * reference expansion
    * limit/offset
* UpdateCommands
* DeleteCommands
* deal with Nullable/Optional

### parallel calls
* async + map/flatMap
* join the right way (bot use get) or better with timeout
* ReactiveStreamUtils

### Errors and Logging
* Sphere Errors
* Exception Hierarchy
* examples for good exception handling
* logging
    * levels
    * activate specific loggers

### Products
* includes not search
* catalogs
* masterVariant and variants -> getAllVariants()
* current vs. staged
* ProductProjection vs. Product
* product types
* dynamic attributes

### Unit Testing
* fake sphere client
* impex

### ProductProjectionSearch

### Format Money/Date/LocalizedStrings

### Custom Fields

### Custom Objects

### Cart
* create
* add line items
* add customer data
* create order

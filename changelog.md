## 0.5.0

Enable multi-tenancy. The bucket is now derived from the tenant claim on the caller's token:
`memory-trevorism` by default, `memory-<tenant-guid>` for a tenant. A tenant bucket is created on
first write. Listing the stored kinds now requires authentication so that it reports the caller's
own tenant.

## 0.4.0

Upgrade dependencies. 

## 0.3.0

Upgrade to Micronaut 5.0.0, Java 25, Gradle 9.x shadow plugin, and updated dependencies.

## 0.2.0

Move to cloud storage implementation to store the json files.

## 0.1.0

Initial implementation.

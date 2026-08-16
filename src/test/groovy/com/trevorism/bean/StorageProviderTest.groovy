package com.trevorism.bean

import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.ServerAuthentication
import org.junit.jupiter.api.Test

class StorageProviderTest {

    @Test
    void testBucketNameDefaultsToTheSharedTenant() {
        assert "memory-trevorism" == new StorageProvider().bucketName
    }

    @Test
    void testBucketNameIsScopedToTheTenantClaim() {
        StorageProvider provider = new StorageProvider()

        provider.setRequest(requestWithTenant("2f9a1c40-0d3e-4a11-9c37-6b5c8e2f7a91"))

        assert "memory-2f9a1c40-0d3e-4a11-9c37-6b5c8e2f7a91" == provider.bucketName
    }

    @Test
    void testBucketNameFallsBackWhenTheRequestIsUnauthenticated() {
        StorageProvider provider = new StorageProvider()

        provider.setRequest(requestWithoutAuthentication())

        assert "memory-trevorism" == provider.bucketName
    }

    @Test
    void testBucketNameFallsBackWhenTheTokenCarriesNoTenant() {
        StorageProvider provider = new StorageProvider()

        provider.setRequest(requestWithTenant(null))

        assert "memory-trevorism" == provider.bucketName
    }

    @Test
    void testTenantBucketNeverCollidesWithTheBucketService() {
        StorageProvider provider = new StorageProvider()
        provider.setRequest(requestWithTenant("abc"))

        assert !provider.bucketName.startsWith("trevorism")
    }

    private static HttpRequest requestWithTenant(String tenant) {
        ServerAuthentication authentication = new ServerAuthentication("app", [], [tenant: tenant])
        return [getAttribute: { String name, Class type -> Optional.of(authentication) }] as HttpRequest
    }

    private static HttpRequest requestWithoutAuthentication() {
        return [getAttribute: { String name, Class type -> Optional.empty() }] as HttpRequest
    }
}

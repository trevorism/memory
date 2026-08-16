package com.trevorism.bean

import com.google.cloud.storage.Bucket
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageException
import com.google.cloud.storage.StorageOptions
import io.micronaut.http.HttpRequest
import io.micronaut.runtime.http.scope.RequestAware
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.security.authentication.ServerAuthentication

@RequestScope
class StorageProvider implements RequestAware {

    private static final String GCP_DEFAULT_PROJECT = "trevorism-data"
    private static final String BUCKET_PREFIX = "memory"
    private static final String DEFAULT_TENANT = "trevorism"
    private static final int BUCKET_ALREADY_EXISTS = 409

    private String tenant
    private Storage storage
    private Bucket bucket

    Storage getStorage() {
        if (!storage) {
            storage = StorageOptions.newBuilder().setProjectId(GCP_DEFAULT_PROJECT).build().getService()
        }
        return storage
    }

    String getBucketName() {
        if (tenant) {
            return "${BUCKET_PREFIX}-${tenant}"
        }
        return "${BUCKET_PREFIX}-${DEFAULT_TENANT}"
    }

    Bucket getBucket() {
        if (!bucket) {
            bucket = getStorage().get(getBucketName())
        }
        return bucket
    }

    Bucket getOrCreateBucket() {
        if (getBucket()) {
            return bucket
        }
        try {
            bucket = getStorage().create(BucketInfo.of(getBucketName()))
        } catch (StorageException e) {
            if (e.code != BUCKET_ALREADY_EXISTS) {
                throw e
            }
            bucket = getStorage().get(getBucketName())
        }
        return bucket
    }

    @Override
    void setRequest(HttpRequest<?> request) {
        Optional<ServerAuthentication> wrappedTenant = request.getAttribute("micronaut.AUTHENTICATION", ServerAuthentication)
        if (wrappedTenant.isPresent())
            tenant = wrappedTenant.get()?.attributes?.get("tenant")
    }
}

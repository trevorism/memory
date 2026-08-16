package com.trevorism.service

import com.google.cloud.storage.Bucket
import com.trevorism.bean.StorageProvider
import org.junit.jupiter.api.Test

class CloudStorageDataRepositoryTest {

    private static final StorageProvider MISSING_BUCKET = new StorageProvider() {
        @Override
        Bucket getBucket() {
            return null
        }
    }

    @Test
    void testTypesAreEmptyBeforeTheTenantBucketExists() {
        assert [] == new CloudStorageDataRepository(MISSING_BUCKET).getTypes()
    }

    @Test
    void testReadAllIsEmptyBeforeTheTenantBucketExists() {
        assert [] == new CloudStorageDataRepository(MISSING_BUCKET).readAll("task")
    }

    @Test
    void testReadIsEmptyBeforeTheTenantBucketExists() {
        assert [:] == new CloudStorageDataRepository(MISSING_BUCKET).read("task", "123")
    }

    @Test
    void testDeleteIsEmptyBeforeTheTenantBucketExists() {
        assert [:] == new CloudStorageDataRepository(MISSING_BUCKET).delete("task", "123")
    }

    @Test
    void testUpdateIsEmptyBeforeTheTenantBucketExists() {
        assert [:] == new CloudStorageDataRepository(MISSING_BUCKET).update("task", "123", [name: "first"])
    }
}

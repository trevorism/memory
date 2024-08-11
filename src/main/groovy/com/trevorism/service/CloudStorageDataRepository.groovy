package com.trevorism.service

import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder

@jakarta.inject.Singleton
class CloudStorageDataRepository implements DataRepository {

    private static final String GCP_DEFAULT_PROJECT = "trevorism-data"
    private static final String DEFAULT_BUCKET_NAME = "memory-trevorism"
    private Storage storage = StorageOptions.newBuilder().setProjectId(GCP_DEFAULT_PROJECT).build().getService()
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create()

    @Override
    List<String> getTypes() {
        Bucket bucket = storage.get(DEFAULT_BUCKET_NAME)
        List<String> fileNames = []
        bucket.list().iterateAll().each { Blob blob ->
            if(!blob.getName().endsWith("/")) {
                fileNames.add(blob.getName())
            }
        }
        return fileNames
    }

    @Override
    Map<String, Object> create(String kind, Map<String, Object> data) {
        kind = kind.toLowerCase()
        addIdIfItDoesNotExist(data)
        List<Map<String, Object>> items = readAll(kind)
        if (!items) {
            writeFullFile(kind, [data])
            return data
        }
        def existing = items.find() { it.id == data.id }
        if (existing) {
            throw new RuntimeException("Item with id ${data.id} already exists")
        }

        items << data
        writeFullFile(kind, items)
        return data

    }

    @Override
    Map<String, Object> read(String kind, String id) {
        kind = kind.toLowerCase()
        def item = readAll(kind).find { it.id == id }
        if (!item) {
            return [:]
        }
        return item;
    }

    @Override
    List<Map<String, Object>> readAll(String kind) {
        kind = kind.toLowerCase()
        Bucket bucket = storage.get(DEFAULT_BUCKET_NAME)
        Blob blob = bucket.get(kind)
        if (blob == null) {
            return []
        }
        String content = new String(blob.getContent(), "UTF-8")
        return gson.fromJson(content, List)
    }

    @Override
    Map<String, Object> update(String kind, String id, Map<String, Object> data) {
        kind = kind.toLowerCase()
        List<Map<String, Object>> items = readAll(kind)
        def existing = items.find() { it.id == id }
        if (existing) {
            data["id"] = id
            items.remove(existing)
            items << data
            writeFullFile(kind, items)
            return data
        }
        return [:]
    }

    @Override
    Map<String, Object> delete(String kind, String id) {
        kind = kind.toLowerCase()
        List<Map<String, Object>> items = readAll(kind)
        def existing = items.find() { it.id == id }
        if (existing) {
            items.remove(existing)
            writeFullFile(kind, items)
            return existing
        }
        return [:]
    }

    private void writeFullFile(String kind, List<Map<String, Object>> items) {
        BlobId blobId = BlobId.of(DEFAULT_BUCKET_NAME, kind)
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build()
        def byteArr = gson.toJson(items).getBytes("UTF-8")
        storage.createFrom(blobInfo, new ByteArrayInputStream(byteArr), createPrecondition(kind))
    }

    private Storage.BlobWriteOption createPrecondition(String path) {
        if (storage.get(DEFAULT_BUCKET_NAME, path) == null) {
            return Storage.BlobWriteOption.doesNotExist()
        } else {
            return Storage.BlobWriteOption.generationMatch(storage.get(DEFAULT_BUCKET_NAME, path).getGeneration())
        }
    }

    private static void addIdIfItDoesNotExist(Map<String, Object> jsonObject) {
        if (!jsonObject["id"])
            jsonObject["id"] = UUID.randomUUID().toString()

    }
}

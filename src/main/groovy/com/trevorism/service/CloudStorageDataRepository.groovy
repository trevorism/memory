package com.trevorism.service

import com.google.cloud.storage.Blob
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageException
import com.google.cloud.storage.StorageOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.trevorism.model.exception.ConflictException

@jakarta.inject.Singleton
class CloudStorageDataRepository implements DataRepository {

    private static final String GCP_DEFAULT_PROJECT = "trevorism-data"
    private static final String DEFAULT_BUCKET_NAME = "memory-trevorism"
    private static final int PRECONDITION_FAILED = 412
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
        Blob blob = readBlob(kind)
        List<Map<String, Object>> items = parseBlob(blob)
        def existing = items.find() { it.id == data.id }
        if (existing) {
            throw new ConflictException("Item with id ${data.id} already exists in ${kind}")
        }

        items << data
        writeFullFile(kind, items, preconditionFor(blob))
        return data
    }

    @Override
    int bulkReplace(String kind, List<Map<String, Object>> data) {
        kind = kind.toLowerCase()
        data.each { addIdIfItDoesNotExist(it) }
        writeFullFile(kind, data, preconditionFor(readBlob(kind)))
        return data.size()
    }

    @Override
    Map<String, Object> read(String kind, String id) {
        kind = kind.toLowerCase()
        def item = readAll(kind).find { it.id == id }
        if (!item) {
            return [:]
        }
        return item
    }

    @Override
    List<Map<String, Object>> readAll(String kind) {
        return parseBlob(readBlob(kind.toLowerCase()))
    }

    @Override
    Map<String, Object> update(String kind, String id, Map<String, Object> data) {
        kind = kind.toLowerCase()
        Blob blob = readBlob(kind)
        List<Map<String, Object>> items = parseBlob(blob)
        def existing = items.find() { it.id == id }
        if (existing) {
            data["id"] = id
            items.remove(existing)
            items << data
            writeFullFile(kind, items, preconditionFor(blob))
            return data
        }
        return [:]
    }

    @Override
    Map<String, Object> delete(String kind, String id) {
        kind = kind.toLowerCase()
        Blob blob = readBlob(kind)
        List<Map<String, Object>> items = parseBlob(blob)
        def existing = items.find() { it.id == id }
        if (existing) {
            items.remove(existing)
            writeFullFile(kind, items, preconditionFor(blob))
            return existing
        }
        return [:]
    }

    private Blob readBlob(String kind) {
        Bucket bucket = storage.get(DEFAULT_BUCKET_NAME)
        return bucket.get(kind)
    }

    private List<Map<String, Object>> parseBlob(Blob blob) {
        if (blob == null) {
            return []
        }
        String content = new String(blob.getContent(), "UTF-8")
        return gson.fromJson(content, List) ?: []
    }

    private void writeFullFile(String kind, List<Map<String, Object>> items, Storage.BlobWriteOption precondition) {
        BlobId blobId = BlobId.of(DEFAULT_BUCKET_NAME, kind)
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build()
        def byteArr = gson.toJson(items).getBytes("UTF-8")
        try {
            storage.createFrom(blobInfo, new ByteArrayInputStream(byteArr), precondition)
        } catch (StorageException e) {
            if (e.code == PRECONDITION_FAILED) {
                throw new ConflictException("${kind} was modified concurrently, the write was not applied", e)
            }
            throw e
        }
    }

    private static Storage.BlobWriteOption preconditionFor(Blob blob) {
        if (blob == null) {
            return Storage.BlobWriteOption.doesNotExist()
        }
        return Storage.BlobWriteOption.generationMatch(blob.getGeneration())
    }

    private static void addIdIfItDoesNotExist(Map<String, Object> jsonObject) {
        if (!jsonObject["id"])
            jsonObject["id"] = UUID.randomUUID().toString()

    }
}

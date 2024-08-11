package com.trevorism.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder

@jakarta.inject.Singleton
class FileDataRepository implements DataRepository {

    public static final String REPO_DIRECTORY = "/tmp/repo"
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create()

    @Override
    List<String> getTypes() {
        ensureRepoDirectory()
        return listFilesInDirectory()
    }

    @Override
    Map<String, Object> create(String kind, Map<String, Object> data) {
        kind = kind.toLowerCase()
        ensureRepoDirectory()
        addIdIfItDoesNotExist(data)
        List<Map<String, Object>> items = readAll(kind)
        if (!items) {
            File file = new File("${REPO_DIRECTORY}/${kind}.json")
            file.text = gson.toJson([data])
            return data
        }
        def existing = items.find() { it.id == data.id }
        if (existing) {
            throw new RuntimeException("Item with id ${data.id} already exists")
        }

        items << data
        File file = new File("${REPO_DIRECTORY}/${kind}.json")
        file.text = gson.toJson(items)
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
        File file = new File("${REPO_DIRECTORY}/${kind}.json")
        if (file.exists()) {
            return gson.fromJson(file.text, List)
        }
        return []
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
            File file = new File("${REPO_DIRECTORY}/${kind}.json")
            file.text = gson.toJson(items)
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
            File file = new File("${REPO_DIRECTORY}/${kind}.json")
            file.text = gson.toJson(items)
            return existing
        }
        return [:]
    }

    private static void ensureRepoDirectory() {
        File directory = new File(REPO_DIRECTORY)
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    private static List<String> listFilesInDirectory() {
        File directory = new File(REPO_DIRECTORY)
        if (directory.exists() && directory.isDirectory()) {
            return directory.listFiles().collect { it.name[0..-6] }
        }
        return []
    }

    private static void addIdIfItDoesNotExist(Map<String, Object> jsonObject) {
        if (!jsonObject["id"])
            jsonObject["id"] = UUID.randomUUID().toString()

    }
}

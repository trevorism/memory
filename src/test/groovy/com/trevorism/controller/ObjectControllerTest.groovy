package com.trevorism.controller

import com.trevorism.service.DataRepository
import io.micronaut.http.exceptions.HttpStatusException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertThrows

class ObjectControllerTest {

    ObjectController objectController = new ObjectController()

    @BeforeEach
    void setup(){
        objectController.repository = new DataRepository() {
            @Override
            List<String> getTypes() {
                return []
            }

            @Override
            Map<String, Object> create(String kind, Map<String, Object> data) {
                return [:]
            }

            @Override
            int bulkReplace(String kind, List<Map<String, Object>> data) {
                return 0
            }

            @Override
            Map<String, Object> read(String kind, String id) {
                return [:]
            }

            @Override
            List<Map<String, Object>> readAll(String kind) {
                return []
            }

            @Override
            Map<String, Object> update(String kind, String id, Map<String, Object> data) {
                return [:]
            }

            @Override
            Map<String, Object> delete(String kind, String id) {
                return [:]
            }
        }
    }

    @Test
    void testGetTypes() {
        assert !objectController.getTypes()
    }

    @Test
    void testRead() {
        assertThrows(HttpStatusException, () -> objectController.read("test", "123"))
    }

    @Test
    void testReadAll() {
        assert !objectController.readAll("test")
    }

    @Test
    void testCreate() {
        assert !objectController.create("test", [:])
    }

    @Test
    void testUpdate() {
        assertThrows(HttpStatusException, () -> objectController.update("test", "123", [:]))
    }

    @Test
    void testDelete() {
        assertThrows(HttpStatusException, () -> objectController.delete("test", "123"))
    }

    @Test
    void testBulkCreate() {
        assert !objectController.bulkCreate("test", [])
    }
}

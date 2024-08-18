package com.trevorism.controller

import com.trevorism.secure.Permissions
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
import com.trevorism.service.DataRepository
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.exceptions.HttpStatusException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller("/object")
class ObjectController {
    private static final Logger log = LoggerFactory.getLogger(ObjectController.class.name)

    @Inject
    DataRepository repository

    @Tag(name = "Object Operations")
    @Operation(summary = "Get all types")
    @Get(value = "/", produces = MediaType.APPLICATION_JSON)
    List<String> getTypes() {
        return repository.getTypes()
    }

    @Tag(name = "Object Operations")
    @Operation(summary = "Get an object of type {kind} with id {id} **Secure")
    @Get(value = "{kind}/{id}", produces = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true, permissions = Permissions.READ)
    Map<String, Object> read(String kind, String id) {
        def entity = repository.read(kind, id)
        if (!entity)
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "${id} not found")

        return entity
    }

    @Tag(name = "Object Operations")
    @Operation(summary = "Get all objects of type {kind} **Secure")
    @Get(value = "{kind}", produces = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true, permissions = Permissions.READ)
    List<Map<String, Object>> readAll(String kind) {
        def entities = repository.readAll(kind)
        return entities
    }

    @Tag(name = "Object Operations")
    @Operation(summary = "Create an object of type {kind} **Secure")
    @Status(HttpStatus.CREATED)
    @Post(value = "{kind}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true, permissions = Permissions.CREATE)
    Map<String, Object> create(String kind, @Body Map<String, Object> data) {
        try {
            def entity = repository.create(kind, data)
            return entity
        } catch (Exception e) {
            log.error("Unable to create ${kind}", e)
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Unable to create ${kind}")
        }
    }

    @Tag(name = "Object Operations")
    @Operation(summary = "Create a list of objects of type {kind} **Secure")
    @Status(HttpStatus.CREATED)
    @Put(value = "{kind}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true, permissions = Permissions.CREATE)
    int bulkCreate(String kind, @Body List<Map<String, Object>> data) {
        try {
            def entity = repository.bulkReplace(kind, data)
            return entity
        } catch (Exception e) {
            log.error("Unable to create ${kind}", e)
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Unable to create ${kind}")
        }
    }

    @Tag(name = "Object Operations")
    @Operation(summary = "Update an object of type {kind} with id {id} **Secure")
    @Put(value = "{kind}/{id}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true, permissions = Permissions.UPDATE)
    Map<String, Object> update(String kind, String id, @Body Map<String, Object> data) {
        def entity = repository.update(kind, id, data)
        if (!entity)
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "${id} not found")
        return entity
    }

    @Tag(name = "Object Operations")
    @Operation(summary = "Delete an object of type {kind} with id {id} **Secure")
    @Delete(value = "{kind}/{id}", produces = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true, permissions = Permissions.DELETE)
    Map<String, Object> delete(String kind, String id) {
        def entity = repository.delete(kind, id)
        if (!entity)
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "${id} not found")
        return entity
    }

}

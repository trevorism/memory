package com.trevorism.controller

import com.trevorism.model.Describe
import com.trevorism.service.DescribeService
import com.trevorism.service.InMemoryDescribeService
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag


@Controller("/describe")
class DescribeController {

    private DescribeService service = new InMemoryDescribeService()

    @Tag(name = "Describe Operations")
    @Operation(summary = "Perform a describe data operation")
    @Post(value = "/", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    def operate(@Body Describe query) {
        service.describe(null)
    }

    @Tag(name = "Describe Operations")
    @Operation(summary = "Get a description of the performable data actions")
    @Get(value = "{id}", produces = MediaType.APPLICATION_JSON)
    def operateById(String id) {
        service.describe(null)
    }

    @Tag(name = "Describe Operations")
    @Operation(summary = "Get a description of the performable data actions")
    @Get(value = "/", produces = MediaType.APPLICATION_JSON)
    def describe() {
        service.describe(null)
    }
}

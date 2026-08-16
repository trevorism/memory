package com.trevorism.controller

import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Test

/**
 * @author tbrooks
 */
class RootControllerTest {

    @Test
    void testRootControllerEndpoints(){
        RootController rootController = new RootController()
        assert rootController.index().getBody().get().contains("/help")
    }

    @Test
    void testRootControllerPing(){
        RootController rootController = new RootController()
        assert rootController.ping() == "pong"
    }

    @Test
    void testRootControllerVersion(){
        RootController rootController = new RootController()
        assert rootController.version()
    }

    @Test
    void testRootControllerHelpRedirectsToTheSwaggerUi(){
        RootController rootController = new RootController()
        def response = rootController.help()

        assert HttpStatus.MOVED_PERMANENTLY == response.status
        assert response.header("Location").contains("swagger-ui")
    }
}

package com.trevorism.controller

import com.trevorism.model.Describe
import org.junit.jupiter.api.Test

class DescribeControllerTest {

    DescribeController describeController = new DescribeController()

    @Test
    void testDescribeListsEveryPerformableAction() {
        assert ["list", "create", "read", "update", "delete"] == describeController.describe()
    }

    @Test
    void testDescribeById() {
        assert ["list", "create", "read", "update", "delete"] == describeController.operateById("123")
    }

    @Test
    void testOperate() {
        assert ["list", "create", "read", "update", "delete"] == describeController.operate(new Describe(id: "123", lookup: "task"))
    }
}

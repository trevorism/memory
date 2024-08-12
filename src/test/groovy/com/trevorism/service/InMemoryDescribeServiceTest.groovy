package com.trevorism.service

import com.trevorism.model.Describe
import org.junit.jupiter.api.Test

class InMemoryDescribeServiceTest {

    @Test
    void testDescribe() {
        assert new InMemoryDescribeService().describe().contains("list")
        assert new InMemoryDescribeService().describe(new Describe()).contains("create")
        assert new InMemoryDescribeService().describe(new Describe([id: "x", "lookup": "z"])).contains("read")
        assert new InMemoryDescribeService().describe().contains("delete")


    }
}

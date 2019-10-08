package com.mtw.supplier.region

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class RegionTests {
    @Test
    fun testSerializationToJson() {
        val r = Region()
        r.lol()
    }
}

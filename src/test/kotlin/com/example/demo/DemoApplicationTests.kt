package com.example.demo

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.springframework.boot.test.context.SpringBootTest

@Disabled("CI: skip context load until test DB is configured")
@SpringBootTest
class DemoApplicationTests {

	@Test
	fun contextLoads() {
	}

}

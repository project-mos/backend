package com.mos.backend;

import com.mos.backend.testconfig.AbstractTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
class BackendApplicationTests extends AbstractTestContainer {

	@Test
	@DirtiesContext
	void contextLoads() {
	}

}

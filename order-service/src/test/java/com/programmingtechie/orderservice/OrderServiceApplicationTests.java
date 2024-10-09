package com.programmingtechie.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
				properties = {
								"eureka.client.enabled=false"
				})
@Transactional(propagation = NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderServiceApplicationTests extends MySqlTestBase{

	@Test
	void contextLoads() {
	}

}

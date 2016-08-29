package br.com.alvesfred.ws.integration.test.processo;

import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import br.com.alvesfred.ws.test.BaseSpringBootApplicationStartup;

/**
 * Integration Test (Mocks)
 *
 * @author alvesfred
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BaseSpringBootApplicationStartup.class)
@WebAppConfiguration
@IntegrationTest("server.port:7070")
public class WSMockITest {

	/*
	 * URL MOCK
	 */
	private static final String INFO_RESOURCE_MOCK = "/infoMock";

	@Value("${local.server.port:7070}")
	private int serverPort;

	@Before
	public void setUp() {
		RestAssured.port = serverPort; // Spring Boot Local - Mock
	}

	@Test
	public void getInfoMock() {
		final Response res = get(INFO_RESOURCE_MOCK);
		assertEquals(200, res.getStatusCode());

		String json = res.asString();
		assertThat(json, containsString("Mock Info Test"));
	}
}

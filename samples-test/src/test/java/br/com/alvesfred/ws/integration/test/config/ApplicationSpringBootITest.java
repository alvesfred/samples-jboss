package br.com.alvesfred.ws.integration.test.config;

import static com.jayway.restassured.RestAssured.when;

import org.fluentlenium.adapter.FluentTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import br.com.alvesfred.ws.test.BaseSpringBootApplicationStartup;

/**
 * WebDriver + Phanton 
 *
 * @author alvesfred
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BaseSpringBootApplicationStartup.class)
@WebAppConfiguration
@IntegrationTest("server.port:7070")
public class ApplicationSpringBootITest extends FluentTest {

	@Value("${local.server.port:7070}")
	private int serverPort;

	// ATENCAO: por conta do Driver do firefox o browser eh inicializado e o cursor de teste espera por acoes
	//private WebDriver webDriver = new PhantomJSDriver();
	private WebDriver webDriver = new FirefoxDriver();

	@Before
	public void setUp() {
		webDriver.get("http://google.com");
        System.out.println(webDriver.getPageSource());
	}

	@Override
	public WebDriver getDefaultDriver() {
		return webDriver;
	}

	protected String getUrl() {
		return "http://localhost:" + serverPort;
	}

	@Test
	public void hasPageTitleMock() {
		when().
			get(getUrl() + "/homeMock").
		then().
			assertThat().
			statusCode(200);
	}

}

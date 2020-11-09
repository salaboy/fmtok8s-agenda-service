package com.salaboy.conferences.agenda.rest;

import com.salaboy.conferences.agenda.rest.ContractVerifierBase;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.Rule;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;

import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.*;
import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static io.restassured.RestAssured.*;

@SuppressWarnings("rawtypes")
public class ContractVerifierTest extends ContractVerifierBase {

	@Test
	public void validate_should_accept_POST_with_new_Proposal() throws Exception {
		// given:
			RequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"title\":\"JQWUBFJWKYUDJYRRXJAB\",\"author\":\"GXZIMVTYQJGTZMTQUDQE\",\"day\":\"RFDIOMDPBOYNFPROQRLC\",\"time\":\"RDYURHWFIDQONTOISINV\"}");

		// when:
			Response response = given().spec(request)
					.post("/");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).matches("text/plain.*");

		// and:
			String responseBody = response.getBody().asString();
			assertThat(responseBody).isEqualTo("Agenda Item Added to Agenda");
	}

}

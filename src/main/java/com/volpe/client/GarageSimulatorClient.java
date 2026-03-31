package com.volpe.client;

import com.volpe.dto.GarageResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GarageSimulatorClient {

	private final RestClient restClient;

	public GarageSimulatorClient(final RestClient garageRestClient) {
		this.restClient = garageRestClient;
	}

	public GarageResponse fetchGarage() {
		return restClient.get().uri("/garage").retrieve().body(GarageResponse.class);
	}
}

package com.volpe.controller;

import com.volpe.dto.WebhookRequest;
import com.volpe.service.WebhookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

	private final WebhookService webhookService;

	public WebhookController(final WebhookService webhookService) {
		this.webhookService = webhookService;
	}

	@PostMapping
	public ResponseEntity<?> handleEvent(@RequestBody @Valid final WebhookRequest request) {
		webhookService.process(request);
		return ResponseEntity.ok().build();
	}
}

package com.example.ddakdaegi.domain.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

	@GetMapping("/health")
	public String healthCheck() {
		return "OK";
	}

	@GetMapping("/versions")
	public String version() {
		return "v3";
	}
}

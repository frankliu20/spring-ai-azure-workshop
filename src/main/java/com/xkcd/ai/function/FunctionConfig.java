package com.xkcd.ai.function;

import java.util.Map;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class FunctionConfig {
	public static final Map<Patient, HealthStatus> HEALTH_DATA = Map.of(
			new Patient("P001"), new HealthStatus("Healthy"),
			new Patient("P002"), new HealthStatus("Has cough"),
			new Patient("P003"), new HealthStatus("Healthy"),
			new Patient("P004"), new HealthStatus("Has increased blood pressure"),
			new Patient("P005"), new HealthStatus("Healthy"));

	@Bean
	@Description("Get patient health status")
	public Function<Patient, HealthStatus> retrievePatientHealthStatus() {
		return (patient) -> new HealthStatus(HEALTH_DATA.get(patient).status());
	}
}

record Patient(String patientId) {
}

record HealthStatus(String status) {
}

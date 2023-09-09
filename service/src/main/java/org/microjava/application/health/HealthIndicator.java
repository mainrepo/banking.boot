package org.microjava.application.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * The HealthIndicator is for checking critical parameters of the service and providing report on that.
 *
 * @author  Gaurav J.
 * @version 1.0
 * @since   May 17, 2019
 *
 */

@Component
public class HealthIndicator implements ReactiveHealthIndicator   {
	
	@Override
	public Mono<Health> health() {
		Health.Builder builder = new Health.Builder();
		builder.status(new Status("BLANK", "no checks required"));
		return Mono.just(builder.build());
	}
	 
}

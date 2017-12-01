package hr.tel.fer.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;



/*
 * Spring po defaultu preko REST-a salje JSON objekte koji NE sadrze id objekta. RepositoryConfig ovveride metodu
 * configureReposirotyRestConfiguration te omogućuje prikaz id-a u JSON-u
 */
@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {
	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Window.class);
    }
}
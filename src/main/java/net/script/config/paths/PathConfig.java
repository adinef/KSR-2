package net.script.config.paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class PathConfig {
    @PathInjection(PathType.FUNCTIONS)
    @Bean
    public Path functionsPath() {
        return Paths.get("f_settings.xml");
    }

    @PathInjection(PathType.QUANTIFIERS)
    @Bean
    public Path quantifiersPath() {
        return Paths.get("q_settings.xml");
    }

    @PathInjection(PathType.QUALIFIERS)
    @Bean
    public Path qualifiersPath() {
        return Paths.get("qf_settings.xml");
    }
}

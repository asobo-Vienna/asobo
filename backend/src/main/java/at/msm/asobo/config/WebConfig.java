package at.msm.asobo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final FileStorageProperties props;

    public WebConfig(FileStorageProperties props) {
        this.props = props;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // configured in application-properties: app.file-storage.base-path=uploads
        String basePath = props.getBasePath();

        if (basePath == null || basePath.isBlank()) {
            // otherwise tests would crash if path is not set
            return;
        }

        String resourceLocation = "file:" + Paths.get(basePath).toAbsolutePath() + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}
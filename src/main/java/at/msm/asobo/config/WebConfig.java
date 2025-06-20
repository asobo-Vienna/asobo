package at.msm.asobo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final FileStorageProperties props;

    public WebConfig(FileStorageProperties props) {
        this.props = props;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String basePath = Paths.get(props.getBasePath()).toAbsolutePath().toString();
        System.out.println("Serving uploads from: " + basePath);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + basePath + "/");
    }

    @PostConstruct
    public void init() {
        System.out.println("WebConfig initialized");
    }
}
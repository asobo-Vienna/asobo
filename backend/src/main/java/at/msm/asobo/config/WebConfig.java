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
        Path rootPath = Paths.get(System.getProperty("user.dir")).getParent(); // go up from 'backend'

        Path uploadPath = rootPath.resolve("uploads").normalize();

        String resourceLocation = "file:" + uploadPath + "/";
        System.out.println("Serving uploads from: " + resourceLocation);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}
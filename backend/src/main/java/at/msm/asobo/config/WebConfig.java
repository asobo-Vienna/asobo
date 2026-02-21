/*package at.msm.asobo.config;


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

    registry.addResourceHandler("/uploads/**").addResourceLocations(resourceLocation);
  }
}*/

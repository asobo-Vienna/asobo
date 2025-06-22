package at.msm.asobo.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.catalina.connector.Connector;

//@Configuration
//public class HttpsRedirectConfig {
//
//    @Bean
//    public ServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
//        factory.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());
//        return factory;
//    }
//
//    private Connector httpToHttpsRedirectConnector() {
//        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
//        connector.setScheme("http");
//        connector.setPort(8080); // HTTP
//        connector.setSecure(false);
//        connector.setRedirectPort(8443); // Redirect to HTTPS
//        return connector;
//    }
//}


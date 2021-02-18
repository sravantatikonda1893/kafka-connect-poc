package com.poc.interfaces;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author sravantatikonda
 */
@Slf4j
@EnableTransactionManagement
@SpringBootApplication
public class CmDataInterfacesApplication {

  private static final String SERVER_PORT = "server.port";

  public static void main(String[] args) {
    log.info("");
    log.info(
        "***********************************************************************************");
    log.info(
        "** U T I L S  S E R V I C E **");
    log.info(
        "***********************************************************************************");
    log.info("");

    Environment env = new SpringApplicationBuilder(CmDataInterfacesApplication.class).run(args)
        .getEnvironment();
    String protocol = "http";

    if (env.getProperty("server.ssl.key-store") != null) {
      protocol = "https";
    }

    log.info(
        "\n----------------------------------------------------------\n\t"
            + "Application '{}' is running! Access URLs:\n\t"
            + "Local HTTP : \t\thttp://localhost:9999/swagger-ui.html\n\t"
            + "\n----------------------------------------------------------\n\t",
        env.getProperty("spring.application.name"), protocol, env.getProperty(SERVER_PORT));

  }


}

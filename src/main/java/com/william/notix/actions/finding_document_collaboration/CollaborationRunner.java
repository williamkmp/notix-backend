package com.william.notix.actions.finding_document_collaboration;

import jakarta.annotation.PreDestroy;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CollaborationRunner implements CommandLineRunner {

    @Value("${server.collaboration.port}")
    private Long collaborationServerPort;

    @Value("${server.name}")
    private String applicationName;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    private Process collaborationServerProcess;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting... collaboration server");

        this.collaborationServerProcess =
            new ProcessBuilder(
                "bun",
                "run",
                "index.ts",
                "--dbHost",
                extractHostFromUrl(databaseUrl),
                "--dbPort",
                extractPortFromUrl(databaseUrl),
                "--dbName",
                extractDatabaseNameFromUrl(databaseUrl),
                "--dbUser",
                databaseUsername,
                "--dbPass",
                databasePassword,
                "--appName",
                applicationName,
                "--appPort",
                collaborationServerPort.toString()
            )
                .start();
    }

    @PreDestroy
    public void destroy() throws Exception {
        if (
            Objects.nonNull(collaborationServerProcess) &&
            collaborationServerProcess.isAlive()
        ) {
            collaborationServerProcess.destroy();
        }
        log.info("Collaboration server stopped");
    }

    private String extractPortFromUrl(String url) {
        String regex = ":(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("unparseable datasource port url:" + url);
    }

    private String extractDatabaseNameFromUrl(String url) {
        String regex =
            "jdbc:(mysql|postgresql|oracle|sqlserver)://[^:/]+(?::\\d+)?(?:;databaseName=|/)([^?;]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(2);
        }
        throw new RuntimeException(
            "unparseable datasource database url:" + url
        );
    }

    private String extractHostFromUrl(String url) {
        String regex =
            "jdbc:(mysql|postgresql|oracle|sqlserver):\\/\\/([^:/]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(2);
        }
        throw new RuntimeException("unparseable datasource host url:" + url);
    }
}

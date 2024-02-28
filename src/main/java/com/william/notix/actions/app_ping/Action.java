package com.william.notix.actions.app_ping;

import com.william.notix.dto.response.Response;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.boot.system.JavaVersion;
import org.springframework.core.SpringVersion;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("pingAction")
public class Action {

    @GetMapping("/ping")
    public Response<Object> action() {
        return new Response<Object>(HttpStatus.OK)
            .setData(
                Map.ofEntries(
                    Map.entry("application", "Notix"),
                    Map.entry(
                        "java-version",
                        JavaVersion.getJavaVersion().toString()
                    ),
                    Map.entry(
                        "server-version",
                        "spring-" + SpringVersion.getVersion()
                    ),
                    Map.entry("server-time", OffsetDateTime.now())
                )
            );
    }
}

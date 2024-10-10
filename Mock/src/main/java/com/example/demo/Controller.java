package com.example.demo;

import com.example.demo.Wrappers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    private ResourceLoader resourceLoader;

        private String getFileContent(String filePath) throws IOException {
            Resource resource = resourceLoader.getResource("classpath:" + filePath);

            try (InputStream inputStream = resource.getInputStream();
                 Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                return scanner.useDelimiter("\\A").next();
            }
        }

    @PostMapping("/buy")
    public ResponseEntity<String> postRequest(@Valid @RequestBody Buy requestBody) throws InterruptedException {
        Thread.sleep(500);

        if ( requestBody.getPrice() == null || requestBody.getPrice().isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("BAD_REQUEST: 'Price' не может быть пустым");
        }

        String response;

        long currentTimestamp = System.currentTimeMillis();
        String method = "POST";
        String uri = "/buy";

        try {
            response = getFileContent("request.txt");

            response = response.replace("{message_id}", UUID.randomUUID().toString())
                    .replace("{timestamp}", String.valueOf(currentTimestamp))
                    .replace("{method}", method)
                    .replace("{uri}", uri)
                    .replace("{price}", requestBody.getPrice());;
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при загрузке файла: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}

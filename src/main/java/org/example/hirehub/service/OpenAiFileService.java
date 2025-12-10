package org.example.hirehub.service;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class OpenAiFileService {

    private final WebClient client;

    public OpenAiFileService(WebClient openAiWebClient) {
        this.client = openAiWebClient;
    }

    public String uploadFile(MultipartFile file) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("purpose", "assistants");
            // part name "file" with resource
            builder.part("file", file.getResource())
                    .header("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getOriginalFilename() + "\"");

            Map resp = client.post()
                    .uri("/files")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (resp == null || resp.get("id") == null) {
                throw new RuntimeException("Upload file failed: no id returned");
            }
            return resp.get("id").toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to OpenAI: " + e.getMessage(), e);
        }
    }
}

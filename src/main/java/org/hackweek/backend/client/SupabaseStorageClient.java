package org.hackweek.backend.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class SupabaseStorageClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String supabaseServiceRoleKey;

    public UploadResult upload(String bucket, String objectPath, MultipartFile file)
        throws IOException, InterruptedException {

        String uploadUrl = stripTrailingSlash(supabaseUrl)
            + "/storage/v1/object/"
            + bucket
            + "/"
            + objectPath;

        String contentType = Objects.requireNonNullElse(file.getContentType(), "application/octet-stream");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uploadUrl))
            .header("Authorization", "Bearer " + supabaseServiceRoleKey)
            .header("apikey", supabaseServiceRoleKey)
            .header("Content-Type", contentType)
            .header("x-upsert", "true")
            .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException(
                "Supabase upload failed: " + response.statusCode() + " - " + response.body()
            );
        }

        String publicUrl = stripTrailingSlash(supabaseUrl)
            + "/storage/v1/object/public/"
            + bucket
            + "/"
            + objectPath;

        return new UploadResult(objectPath, publicUrl);
    }

    private static String stripTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public record UploadResult(String objectPath, String publicUrl) {}
}
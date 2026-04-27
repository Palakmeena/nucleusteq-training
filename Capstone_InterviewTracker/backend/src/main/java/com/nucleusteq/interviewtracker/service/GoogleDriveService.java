package com.nucleusteq.interviewtracker.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Real service for Google Drive integration.
 * Uses a Service Account JSON key to upload files to a shared folder.
 */
@Service
public class GoogleDriveService {

    @Value("${google.drive.folder.id:}")
    private String folderId;

    private static final String APPLICATION_NAME = "Interview Tracker";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    /**
     * Initializes the Google Drive service using the Service Account JSON key.
     * The key file should be placed in src/main/resources/service-account-key.json
     */
    private Drive getDriveService() throws IOException, GeneralSecurityException {
        InputStream in = GoogleDriveService.class.getResourceAsStream("/service-account-key.json");
        if (in == null) {
            throw new IOException("Google Service Account Key not found at /service-account-key.json");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(SCOPES);

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Uploads a file to a specific Google Drive folder.
     * Sets the file to be viewable by anyone with the link (for HR preview).
     *
     * @param multipartFile the file from the candidate profiling form
     * @return the webViewLink for the uploaded file
     */
    public String uploadFile(MultipartFile multipartFile) throws IOException, GeneralSecurityException {
        Drive service = getDriveService();

        File fileMetadata = new File();
        fileMetadata.setName(multipartFile.getOriginalFilename());
        if (folderId != null && !folderId.isEmpty()) {
            fileMetadata.setParents(Collections.singletonList(folderId));
        }

        java.io.File tempFile = java.io.File.createTempFile("resume-", ".pdf");
        multipartFile.transferTo(tempFile);

        FileContent mediaContent = new FileContent("application/pdf", tempFile);
        
        File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        // Optional: Clean up temp file
        tempFile.delete();

        return file.getWebViewLink();
    }
}

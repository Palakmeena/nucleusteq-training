package com.nucleusteq.interviewtracker.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Google Drive integration using OAuth 2.0.
 * Uses client_secret.json for OAuth authentication.
 */
@Service
public class GoogleDriveService {

    @Value("${google.drive.folder.id:}")
    private String folderId;

    private static final String APPLICATION_NAME = "Interview Tracker";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Creates an authorized Credential object.
     * Uses OAuth 2.0 with tokens stored locally.
     */
    private Credential getCredentials() throws IOException, GeneralSecurityException {
        // Load client secret from OAuth credentials file
        InputStream in = GoogleDriveService.class.getResourceAsStream("/client_secret.json");
        if (in == null) {
            throw new IOException("OAuth credentials file not found: /client_secret.json");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get(TOKENS_DIRECTORY_PATH).toFile()))
                .setAccessType("offline")
                .build();

        // Returns an authorized Credential object
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Build and return an authorized Drive client service.
     */
    private Drive getDriveService() throws IOException, GeneralSecurityException {
        Credential credential = getCredentials();
        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Uploads a file to the configured Google Drive folder.
     * Uses OAuth authentication.
     * Makes the file publicly viewable by anyone with the link.
     *
     * @param multipartFile the file to upload (resume)
     * @return the webViewLink for the uploaded file
     */
    public String uploadFile(MultipartFile multipartFile) throws IOException, GeneralSecurityException {
        Drive service = getDriveService();

        // Create file metadata
        File fileMetadata = new File();
        fileMetadata.setName(multipartFile.getOriginalFilename());
        if (folderId != null && !folderId.isEmpty()) {
            fileMetadata.setParents(Collections.singletonList(folderId));
        }

        // Create temp file from multipart
        java.io.File tempFile = java.io.File.createTempFile("resume-", ".pdf");
        multipartFile.transferTo(tempFile);

        // Upload file
        FileContent mediaContent = new FileContent("application/pdf", tempFile);
        File file = service.files().create(fileMetadata, mediaContent)
                .setSupportsAllDrives(true)
                .setFields("id, webViewLink")
                .execute();

        // Make file publicly viewable (anyone with link can view)
        try {
            Permission publicPermission = new Permission()
                    .setType("anyone")
                    .setRole("reader");
            service.permissions().create(file.getId(), publicPermission)
                    .setSupportsAllDrives(true)
                    .execute();
        } catch (Exception e) {
            // If permission fails, log but don't break upload
            System.err.println("Warning: Could not set public permission: " + e.getMessage());
        }

        // Clean up temp file
        tempFile.delete();

        // Return embeddable preview URL that works with public permissions
        // This format works in iframes for publicly shared files
        String fileId = file.getId();
        return "https://drive.google.com/file/d/" + fileId + "/preview";
    }
}

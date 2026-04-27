package com.nucleusteq.interviewtracker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Mock service for Google Drive integration.
 * In a real-world scenario, this service would use the Google Drive API
 * and a Service Account JSON to upload files to a specific folder.
 */
@Service
public class GoogleDriveService {

    /**
     * Simulates uploading a file to Google Drive.
     *
     * @param file the file to upload
     * @return a mock webViewLink that would normally be returned by Drive API
     */
    public String uploadFile(MultipartFile file) {
        // Generating a unique mock ID to simulate Google Drive File ID
        String mockFileId = UUID.randomUUID().toString();
        
        // This is a standard Google Drive preview URL format
        return "https://drive.google.com/file/d/" + mockFileId + "/view?usp=sharing&name=" + file.getOriginalFilename();
    }
}

package com.tjandra.dspfileprocessor.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ImageService {

    private static final String APPLICATION_NAME = "DSP Test";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private Credential getCredentials(final NetHttpTransport httpTransport)
            throws IOException {
        InputStream in = ImageService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost("127.0.0.1").setPort(8089).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public Drive getInstance() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void uploadFiles(java.io.File[] fileList) {
        try {
            for (final java.io.File fileEntry : fileList) {
                File fileMetadata = new File();
                fileMetadata.setName(fileEntry.getName());
                FileContent mediaContent = new FileContent("image/jpeg", fileEntry);
                File uploadFile = this.getInstance()
                        .files()
                        .create(fileMetadata, mediaContent)
                        .setFields("id").execute();
                log.info("File ID: {}", uploadFile.getId());
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    public void extractText(java.io.File[] fileList) {
        for (java.io.File imageFile : fileList) {
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("src/main/resources/tessdata");
            tesseract.setLanguage("eng+chi_tra");
            tesseract.setPageSegMode(1);
            tesseract.setOcrEngineMode(1);
            try {
                String result = tesseract.doOCR(imageFile);
                log.info("OCR result for file {}: {}", imageFile.getName(), result);
            } catch (TesseractException e) {
                log.error("Error occurred when trying to read text", e);
            }
        }
    }

    public void processImages(String folderPath) {
        try {
            final java.io.File folder = new java.io.File(folderPath);
            final java.io.File[] fileList = Objects.requireNonNull(folder.listFiles());

            uploadFiles(fileList);
            extractText(fileList);
        } catch (Exception e) {
            log.error("Exception occurred", e);
        }
    }
}

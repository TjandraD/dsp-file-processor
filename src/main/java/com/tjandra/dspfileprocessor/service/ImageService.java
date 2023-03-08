package com.tjandra.dspfileprocessor.service;

import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
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

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
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
        List<String> englishWords = new ArrayList<>();
        List<String> chineseWords = new ArrayList<>();

        for (java.io.File imageFile : fileList) {
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("src/main/resources/tessdata");
            tesseract.setLanguage("eng+chi_tra");
            tesseract.setPageSegMode(1);
            tesseract.setOcrEngineMode(1);
            try {
                String result = tesseract.doOCR(imageFile);
                result = result.trim();
                result = result.replaceAll("\\s+", " ");
                result = result.replaceAll("\n", "");

                for (String word : result.split(" ")) {
                    if (detectLanguage(word).equals(Language.ENGLISH)) {
                        englishWords.add(word);
                    } else if (detectLanguage(word).equals(Language.CHINESE)) {
                        chineseWords.add(word);
                    }
                }

                writeOutputFiles("English Output", englishWords, true);
                writeOutputFiles("Chinese Output", chineseWords, false);
            } catch (TesseractException e) {
                log.error("Error occurred when trying to read text", e);
            }
        }
    }

    public void writeOutputFiles(String fileName, List<String> words, boolean isEnglish) {
        try {
            String filePath = "src/main/resources/output/";
            FileWriter fileWriter = new FileWriter(filePath + fileName + ".md");
            StringBuilder stringBuilder = new StringBuilder();

            for (String word : words) {
                if (isEnglish && word.toLowerCase().contains("o")) {
                    stringBuilder.append("<span style=\"color:blue\">").append(word).append("</span> ");
                } else {
                    stringBuilder.append(word).append(" ");
                }
            }

            fileWriter.write(stringBuilder.toString());
            fileWriter.close();
        } catch (IOException e) {
            log.error("Error occurred when trying to write output", e);
        }
    }

    public Language detectLanguage(String word) {
        final LanguageDetector detector = LanguageDetectorBuilder.fromLanguages(Language.ENGLISH, Language.CHINESE).build();
        return detector.detectLanguageOf(word);
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

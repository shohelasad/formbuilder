package com.vantage.sportsregistration.service;

import com.formreleaf.domain.Document;
import com.formreleaf.domain.DocumentDownloadHistory;
import com.formreleaf.domain.Program;
import com.formreleaf.domain.User;
import com.vantage.sportsregistration.exceptions.UserNotFoundException;
import com.formreleaf.repository.DocumentDownloadHistoryRepository;
import com.formreleaf.repository.DocumentRepository;
import com.formreleaf.common.utils.EncryptionUtils;
import com.formreleaf.common.utils.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;


/**
 * @author Bazlur Rahman Rokon
 * @since 9/3/15.
 */

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static final DecimalFormat TIME_FORMAT_4 = new DecimalFormat("0000;0000");

    @Value(value = "${document.location}")
    private String documentLocation;

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentDownloadHistoryRepository documentDownloadHistoryRepository;

    @Override
    public Document upload(Document document, Long programId, InputStream inputStream) {

        try {
            Document encrypted = encrypt(document, inputStream);
            Program program = programService.findById(programId);
            encrypted.setOrganization(program.getOrganization());
            String url = ServletUtils.getContextURL("media/document/" + encrypted.getUuid());
            encrypted.setUrl(url);

            return userService.findCurrentLoggedInUser().map(user -> {
                encrypted.setOwner(user);

                return documentRepository.save(encrypted);
            }).orElseThrow(UserNotFoundException::new);

        } catch (Exception e) {
            LOGGER.error("Unable to encrypt and save document ", e);
            throw new RuntimeException("Unable to encrypt and save document", e);
        }
    }

    @Override
    public ResponseEntity<byte[]> download(String uuid, HttpServletResponse response) {
        Document document = documentRepository.findByUuid(uuid);
        checkOwnership(document);

        String file = getFilePath(uuid);

        if (!Files.exists(Paths.get(file))) {
            throw new RuntimeException("The file you are looking doesn't exist");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            EncryptionUtils.getInstance().decrypt(file, document.getEncryptionKey(), document.getIv(), outputStream);

            response.setContentType(document.getContentType() + "; charset=UTF-8");
            String fileName = document.getDocumentName().replaceAll("\\s", "_");
            byte[] bytes = outputStream.toByteArray();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(document.getContentType()));
            httpHeaders.set("Content-Disposition", "attachment; filename=" + fileName);
            httpHeaders.setContentLength(bytes.length);

            addDownloadHistory(document);

            return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Unable to decrypt file: ", e);
            throw new RuntimeException("Unable to decrypt file", e);
        }
    }

    @Override
    public void delete(List<String> uuids) {
        uuids.forEach(uuid -> {
            Document document = documentRepository.findByUuid(uuid);
            document.setDeleted(true);
            deletedFile(uuid);

            documentRepository.save(document);
        });
    }

    private void deletedFile(String uuid) {
        String filePath = getFilePath(uuid);

        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            LOGGER.error("Unable to delete file", e);
            throw new RuntimeException("Unable to delete file", e);
        }
    }

    private void addDownloadHistory(Document document) {
        Optional<User> currentLoggedInUser = userService.findCurrentLoggedInUser();
        if (!currentLoggedInUser.isPresent()) {
            throw new UserNotFoundException("Current loggedIn user not found");
        }

        DocumentDownloadHistory downloadHistory = new DocumentDownloadHistory();
        downloadHistory.setDocument(document);
        downloadHistory.setDownlaodTime(new Date());
        downloadHistory.setDownloadedBy(currentLoggedInUser.get().getEmail());

        documentDownloadHistoryRepository.save(downloadHistory);
    }

    private String getFilePath(String uuid) {

        return documentLocation + uuid + ".fr";
    }

    private void checkOwnership(Document document) {
        userService.findCurrentLoggedInUser().map(user -> {
            if (userService.isCurrentLoggedInUserIsOrganization()) {
                if (!document.getOrganization().getId().equals(user.getOrganization().getId())) {
                    throw new SecurityException("You don't have permission to access this document.");
                }
            } else if (!document.getOwner().getId().equals(user.getId())) {
                throw new SecurityException("You don't have permission to access this document.");
            }

            return true;
        }).orElseThrow(UserNotFoundException::new);
    }

    private Document encrypt(Document document, InputStream inputStream) throws Exception {

        SecretKey key = EncryptionUtils.getInstance().generateKey();
        String uuid = getUniqueId();
        String location = getFilePath(uuid);
        String iv = EncryptionUtils.getInstance().encrypt(location, inputStream, key);

        String encodedKey = EncryptionUtils.getInstance().getKeyAsString(key);

        document.setUuid(uuid)
                .setIv(iv)
                .setEncryptionKey(encodedKey);

        return document;
    }

    public static String getUniqueId() {
        Calendar cal = Calendar.getInstance();
        String val = String.valueOf(cal.get(Calendar.YEAR));
        val += TIME_FORMAT_4.format(cal.get(Calendar.DAY_OF_YEAR));
        val += UUID.randomUUID().toString().replaceAll("-", "");

        return val;
    }
}

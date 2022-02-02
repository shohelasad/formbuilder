package com.vantage.sportsregistration.controller.api;

import com.formreleaf.common.utils.FileUtils;
import com.formreleaf.domain.Document;
import com.vantage.sportsregistration.exceptions.DocumentUploadException;
import com.vantage.sportsregistration.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/3/15.
 */
@RestController
@RequestMapping("api/v1/document")
public class DocumentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentService documentService;

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    Document handleFileUpload(@RequestParam(value = "file", required = true) MultipartFile file,
                              @RequestParam(value = "programId", required = true) Long programId) {
        LOGGER.info("handleFileUpload()");

        if (!file.isEmpty()) {
            if (!FileUtils.isValidFile(file, Document.VALID_FILE_TYPE_LIST)) {
                throw new DocumentUploadException("File format is not valid.");
            }

            try {
                InputStream inputStream = file.getInputStream();

                Document document = new Document();
                document.setDocumentName(FileUtils.getFilteredFileName(file.getOriginalFilename()));
                document.setContentType(FileUtils.getContentType(FileUtils.getExtensionInLowerCase(document.getDocumentName())));
                document.setProgramId(programId);

                return documentService.upload(document, programId, inputStream);
            } catch (Exception e) {
                LOGGER.error("Document couldn't be uploaded", e);
                throw new DocumentUploadException("Document couldn't be uploaded", e);
            }
        } else {
            throw new DocumentUploadException("You failed to upload, because the file was empty.");
        }
    }

    @RequestMapping(value = "delete/{uuid}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void removeFile(@PathVariable List<String> uuid) {

        documentService.delete(uuid);
    }
}
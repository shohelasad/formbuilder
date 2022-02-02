package com.vantage.sportsregistration.controller;

import com.formreleaf.repository.SignatureRepository;
import com.vantage.sportsregistration.service.DocumentService;
import com.formreleaf.common.utils.ImageUtils;
import com.formreleaf.common.utils.LongCipher;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author Bazlur Rahman Rokon
 * @since 7/13/15.
 */
@Controller
public class MediaController {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MediaController.class);

    @Autowired
    private SignatureRepository repository;

    @Autowired
    private DocumentService documentService;

    @RequestMapping(value = "media/{key}", method = RequestMethod.GET)
    public void downloadSignature(@PathVariable String key, HttpServletResponse response) {

        long id = LongCipher.getInstance().decrypt(key);

        repository.findOneById(id).ifPresent(signature -> {
            String sign = signature.getSign();
            response.setContentType("image/png");

            try {
                byte[] bytes = ImageUtils.convert(sign);

                response.setContentLength(bytes.length);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + key + ".png\"");
                response.setStatus(HttpStatus.OK.value());
                response.setDateHeader("Last-Modified", signature.getLastModifiedDate().toInstant().toEpochMilli());
                FileCopyUtils.copy(bytes, response.getOutputStream());

            } catch (IOException e) {
                LOGGER.error("unable to download signature", e);
            }
        });
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "media/document/{uuid}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> download(@PathVariable String uuid, HttpServletResponse resp) {

        return documentService.download(uuid, resp);
    }
}

package com.vantage.sportsregistration.service;

import com.formreleaf.domain.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/3/15.
 */
@Component
public interface DocumentService {

    Document upload(Document document, Long programId, InputStream inputStream);

    ResponseEntity<byte[]> download(String uuid, HttpServletResponse response);

    void delete(List<String> uuid);
}

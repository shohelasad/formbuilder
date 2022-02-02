package com.formreleaf.common.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.formreleaf.common.dto.DocumentDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/14/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentField extends Field {
    private List<DocumentDto> documents = new ArrayList<>();

    public List<DocumentDto> getDocuments() {
        return documents;
    }

    public DocumentField setDocuments(List<DocumentDto> documents) {
        this.documents = documents;
        return this;
    }
}

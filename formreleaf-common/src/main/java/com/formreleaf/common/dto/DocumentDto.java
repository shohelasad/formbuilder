package com.formreleaf.common.dto;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/28/15.
 */
public class DocumentDto {
    private String documentName;
    private String contentType;
    private String comment;
    private String uuid;
    private String url;

    public String getDocumentName() {
        return documentName;
    }

    public DocumentDto setDocumentName(String documentName) {
        this.documentName = documentName;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public DocumentDto setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public DocumentDto setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public DocumentDto setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DocumentDto setUrl(String url) {
        this.url = url;
        return this;
    }
}

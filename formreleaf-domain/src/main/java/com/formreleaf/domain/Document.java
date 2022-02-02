package com.formreleaf.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/3/15.
 */
@Entity
public class Document extends BaseEntity<Long> implements NonDeletable {

    public static final String[] VALID_FILE_TYPE_LIST = {"pdf", "doc", "docx","jpg", "jpeg", "png","xls","xlsx","gif"};

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 200)
    private String documentName;

    @JsonIgnore
    @Size(max = 256)
    private String encryptionKey;

    @JsonIgnore
    @Column(length = 32)
    private String iv;

    @Size(max = 100)
    private String contentType;

    @Size(max = 500)
    private String comment;

    @JsonIgnore
    @ManyToOne
    private User owner;

    @JsonIgnore
    @ManyToOne
    private Organization organization;

    @JsonIgnore
    private Long programId;// just a reference

    @Size(max = 40)
    private String uuid;

    @Size(max = 200)
    private String url;

    @JsonIgnore
    private boolean deleted;

    public Document setId(Long id) {
        this.id = id;
        return this;
    }

    public String getDocumentName() {
        return documentName;
    }

    public Document setDocumentName(String documentName) {
        this.documentName = documentName;
        return this;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public Document setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public Document setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public User getOwner() {
        return owner;
    }

    public Document setOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public Document setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public Long getProgramId() {
        return programId;
    }

    public Document setProgramId(Long programId) {
        this.programId = programId;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Document setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Document setUrl(String url) {
        this.url = url;
        return this;
    }

    public Organization getOrganization() {
        return organization;
    }

    public Document setOrganization(Organization organization) {
        this.organization = organization;
        return this;
    }

    public Document setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public String getIv() {
        return iv;
    }

    public Document setIv(String iv) {
        this.iv = iv;
        return this;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Document{");
        sb.append("id=").append(id);
        sb.append(", documentName='").append(documentName).append('\'');
        sb.append(", encryptionKey='").append(encryptionKey).append('\'');
        sb.append(", contentType='").append(contentType).append('\'');
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", owner=").append(owner);
        sb.append(", uuid='").append(uuid).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", content=");
        sb.append(", deleted=").append(deleted);
        sb.append('}');
        return sb.toString();
    }
}

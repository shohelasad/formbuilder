package com.formreleaf.domain;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/8/15.
 */
@Entity
public class DocumentDownloadHistory extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date downlaodTime;

    @Size(max = 100)
    private String downloadedBy;

    @ManyToOne
    private Document document;

    public DocumentDownloadHistory setId(Long id) {
        this.id = id;
        return this;
    }

    public Date getDownlaodTime() {
        return downlaodTime;
    }

    public DocumentDownloadHistory setDownlaodTime(Date downlaodTime) {
        this.downlaodTime = downlaodTime;
        return this;
    }

    public String getDownloadedBy() {
        return downloadedBy;
    }

    public DocumentDownloadHistory setDownloadedBy(String downloadedBy) {
        this.downloadedBy = downloadedBy;
        return this;
    }

    public Document getDocument() {
        return document;
    }

    public DocumentDownloadHistory setDocument(Document document) {
        this.document = document;
        return this;
    }

    @Override
    public Long getId() {

        return id;
    }
}

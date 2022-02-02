package com.formreleaf.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class BaseEntity<ID extends Number & Comparable> implements Comparable<BaseEntity>, Serializable {

    @Version
    private Long version;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "last_modified_date", nullable = false)
    private Date lastModifiedDate;

    public abstract ID getId();

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @PrePersist
    public void prePersist() {
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = new Date();
    }

    @Override
    public int compareTo(BaseEntity object) {

        return getId().compareTo(object.getId());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseEntity{");
        sb.append("version=").append(version);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append('}');
        return sb.toString();
    }
}

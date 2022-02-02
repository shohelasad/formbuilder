package com.formreleaf.domain;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 4/26/15.
 */
public interface NonDeletable {
    Boolean isDeleted();

    void setDeleted(Boolean deleted);
}

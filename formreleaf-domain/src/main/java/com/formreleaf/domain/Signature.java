package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Signature extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 65000, nullable = true)
    private String sign;

    @ManyToOne
    @JoinColumn(name = "agreement_id")
    private Agreement agreement;

    public Signature() {
    }

    public Long getId() {
        return id;
    }

    public Agreement getAgreement() {
        return agreement;
    }

    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signature signature = (Signature) o;
        return Objects.equals(id, signature.id) &&
                Objects.equals(sign, signature.sign) &&
                Objects.equals(agreement, signature.agreement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sign, agreement);
    }

    @Override
    public String toString() {
        return "Signature{" +
                "id=" + id +
                ", sign='" + sign + '\'' +
                ", agreement=" + agreement +
                '}';
    }
}

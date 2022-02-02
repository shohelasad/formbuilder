package com.formreleaf.common.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Option {
    private Integer id;
    private String title;
    private String value;
    private boolean selected;

    public Option() {
    }

    public Option(Integer id, String title, String value) {
        this.id = id;
        this.title = title;
        this.value = value;
    }

    public Option(int id, String title) {
        this.id = id;
        this.title = title;
        this.value = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Option option = (Option) o;
        return Objects.equals(id, option.id) &&
                Objects.equals(title, option.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}

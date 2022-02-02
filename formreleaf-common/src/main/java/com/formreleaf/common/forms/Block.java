package com.formreleaf.common.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Block implements Cloneable {
    private Integer id;
    private String title;
    private List<Field> fields = new ArrayList<>();

    public Block(Integer id, String blockName) {
        this.id = id;
        this.title = blockName;
    }

    public Block(Integer id, String title, List<Field> fields) {
        this.id = id;
        this.title = title;
        this.fields = fields;
    }

    public Block() {
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

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Block cloned = (Block) super.clone();
        cloned.setId(id);
        cloned.setTitle(title);
        cloned.setFields(fields);
        return cloned;
    }
}

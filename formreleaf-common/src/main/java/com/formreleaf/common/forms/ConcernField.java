package com.formreleaf.common.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Bazlur Rahman Rokon
 * @date 6/1/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConcernField extends Field {
    private boolean concerned;
    private boolean isConcernedYes;

    public ConcernField(FieldType textarea, String academic) {
        super(textarea, academic);
        concerned = true;
    }

    public ConcernField(FieldType textarea, String allergies, boolean b) {
        super(textarea, allergies, b);
        concerned = true;
    }

    public Boolean isConcerned() {
        return concerned;
    }

    public void setConcerned(Boolean concerned) {
        this.concerned = concerned;
    }

    public boolean isConcernedYes() {
        return isConcernedYes;
    }

    public void setIsConcernedYes(boolean isConcernedYes) {
        this.isConcernedYes = isConcernedYes;
    }
}

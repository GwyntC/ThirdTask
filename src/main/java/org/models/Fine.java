package org.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class Fine {
    @JsonProperty("fine_amount")
    private Float fineAmount;
    @JsonProperty("type")
    private String type;

    public Float getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Float fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


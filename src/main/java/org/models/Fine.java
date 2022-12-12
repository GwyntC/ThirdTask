package org.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonAutoDetect
public class Fine {
    @JsonProperty("fine_amount")
    private BigDecimal fineAmount;
    @JsonProperty("type")
    private String type;

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void  setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


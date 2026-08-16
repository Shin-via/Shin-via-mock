package com.via.shinviamock.card.dto.mydata;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class CardItem {
    private String cardId;
    private String institutionId;
    private String cardNum;
    private String cardName;
    @JsonProperty("is_consent")
    private final boolean isConsent = true;
    private final String cardMember = "1";
    private final String cardType = "01";
}

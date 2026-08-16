package com.via.shinviamock.card.dto.response;

import lombok.Data;

@Data
public class CardInfoDto {

    private String cardId;
    private String institutionId;
    private String cardNumMasked;
    private String cardName;
    private String cardMemberType;
}

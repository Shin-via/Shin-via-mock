package com.via.shinviamock.common.dto;

import com.via.shinviamock.common.util.TranIdGenerator;
import lombok.Data;

@Data
public class CommonResponseHeader {

    private String apiTranId;
    private String apiTranDtm;
    private String rspCode;
    private String rspMessage;
    private String bankTranId;
    private String bankTranDate;
    private String bankCodeTran;
    private String bankRspCode;
    private String bankRspMessage;
    private String userSeqNo;
    private String nextPageYn;
    private String beforInquiryTraceInfo;

    public void setSuccessHeader(String bankTranId, String bankCodeStd, String userSeqNo, String beforInquiryTraceInfo) {
        this.apiTranId = TranIdGenerator.generateApiTranId();
        this.apiTranDtm = TranIdGenerator.generateApiTranDtm();
        this.rspCode = "A0000";
        this.rspMessage = "";
        this.bankTranId = bankTranId;
        this.bankTranDate = TranIdGenerator.generateBankTranDate();
        this.bankCodeTran = bankCodeStd;
        this.bankRspCode = "000";
        this.bankRspMessage = "";
        this.userSeqNo = userSeqNo;
        this.nextPageYn = "N";
        this.beforInquiryTraceInfo = beforInquiryTraceInfo == null ? "" : beforInquiryTraceInfo;
    }
    public void setSucccessHeaderForList(String xApiTranId){
        this.apiTranId = xApiTranId;
    }
}

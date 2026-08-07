package com.via.shinviamock.common.util;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
@Component
public final class TranIdGenerator {

    private static final DateTimeFormatter API_TRAN_DTM_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final DateTimeFormatter BANK_TRAN_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static StringRedisTemplate redisTemplate;

    public TranIdGenerator(StringRedisTemplate redisTemplate) {
        TranIdGenerator.redisTemplate = redisTemplate;
    }

    public static String generateApiTranId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }

    public static String generateApiTranDtm() {
        return LocalDateTime.now().format(API_TRAN_DTM_FORMAT);
    }

    public static String generateBankTranDate() {
        return LocalDate.now().format(BANK_TRAN_DATE_FORMAT);
    }

    public static String tokenConvertCi(String authToken){
        if (redisTemplate != null && authToken != null) {
            try {
                String ci = redisTemplate.opsForValue().get(authToken);
                if (ci != null) {
                    return ci;
                }
            } catch (Exception ignored) {
            }
        }
        return "mock_user_ci";
    }
}

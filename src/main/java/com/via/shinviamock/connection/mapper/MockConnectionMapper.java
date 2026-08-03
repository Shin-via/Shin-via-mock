package com.via.shinviamock.connection.mapper;

import com.via.shinviamock.connection.dto.MockAuthorizationDto;
import com.via.shinviamock.connection.dto.MockClientDto;
import com.via.shinviamock.connection.dto.MockTransactionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MockConnectionMapper {

    MockClientDto selectClientByClientId(@Param("clientId") String clientId);

    int insertAuthorization(MockAuthorizationDto authorization);

    MockAuthorizationDto selectAuthorizationByCode(@Param("code") String code);

    int updateAuthorizationUsed(@Param("connectionId") Long connectionId);

    int insertTransaction(MockTransactionDto transaction);

    int countTransactionByTranId(@Param("tranId") String tranId);
}

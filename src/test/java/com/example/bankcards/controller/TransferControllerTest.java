package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    @Test
    void transfer_successful_shouldReturn200AndTransferResponse() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        TransferRequest request = new TransferRequest(fromId, toId, BigDecimal.valueOf(100.00));

        TransferResponse serviceResponse = new TransferResponse(
                UUID.randomUUID(),
                fromId,
                toId,
                BigDecimal.valueOf(100.00),
                java.time.LocalDateTime.now(),
                "SUCCESS"
        );

        when(transferService.transfer(request)).thenReturn(serviceResponse);

        ResponseEntity<TransferResponse> response = transferController.transfer(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());  // ← исправлено
        assertEquals(serviceResponse, response.getBody());
        verify(transferService).transfer(request);
    }

}

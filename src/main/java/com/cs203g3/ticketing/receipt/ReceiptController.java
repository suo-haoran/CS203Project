package com.cs203g3.ticketing.receipt;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.receipt.dto.ReceiptRequestDto;
import com.cs203g3.ticketing.receipt.dto.ReceiptResponseDto;
import com.cs203g3.ticketing.security.auth.UserDetailsImpl;

@RestController
@RequestMapping("/v1")
public class ReceiptController {
    
    private ReceiptService receiptService;

    public ReceiptController(ReceiptService rs) {
        this.receiptService = rs;
    }

    @GetMapping("/my-receipts")
    public List<ReceiptResponseDto> getAllReceiptsByCurrentUser(Authentication auth) {
        String username = auth.getName();
        return receiptService.getAllReceiptsByUsername(username);
    }

    @GetMapping("/receipts")
    public List<ReceiptResponseDto> getAllReceipts() {
        return receiptService.getAllReceipts();
    }

    @GetMapping("/receipts/{uuid}")
    public ReceiptResponseDto getReceipt(@PathVariable UUID uuid) {
        return receiptService.getReceipt(uuid);
    }

    @PostMapping("/receipts")
    public ReceiptResponseDto addReceiptAsCurrentUser(Authentication auth, @RequestBody ReceiptRequestDto newReceiptDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return receiptService.addReceiptAsUserId(userDetails.getId(), newReceiptDto);
    }

    @PutMapping("/receipts/{uuid}")
    public ReceiptResponseDto updateReceipt(@PathVariable UUID uuid, @RequestBody ReceiptRequestDto newReceiptDto) {
        return receiptService.updateReceipt(uuid, newReceiptDto);
    }

    @DeleteMapping("/receipts/{uuid}")
    public void deleteReceipt(@PathVariable UUID uuid) {
        receiptService.deleteReceipt(uuid);
    }
}

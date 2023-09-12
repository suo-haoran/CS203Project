package com.cs203g3.ticketing.receipt;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.receipt.dto.ReceiptResponseDto;

@RestController
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

    @GetMapping("/receipts/{id}")
    public ReceiptResponseDto getReceipt(@PathVariable UUID id) {
        return receiptService.getReceipt(id);
    }

    @DeleteMapping("/receipts/{id}")
    public void deleteReceipt(@PathVariable UUID id) {
        receiptService.deleteReceipt(id);
    }

    // @PostMapping("/receipts")
    // public Receipt addReceipt(@RequestBody Receipt newReceipt) {
    //     return receiptService.addReceipt(newReceipt);
    // }

    // @PutMapping("/receipts/{id}")
    // public Receipt updateReceipt(@PathVariable UUID id, @RequestBody Receipt newReceipt) {
    //     return receiptService.updateReceipt(id, newReceipt);
    // }
}

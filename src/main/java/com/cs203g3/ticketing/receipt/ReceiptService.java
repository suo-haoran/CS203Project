package com.cs203g3.ticketing.receipt;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.receipt.dto.ReceiptResponseDto;
import com.cs203g3.ticketing.user.UserRepository;

@Service
public class ReceiptService {
    
    @Autowired
    private ModelMapper modelMapper;

    private ReceiptRepository receipts;
    private UserRepository users;

    public ReceiptService(ReceiptRepository receipts, UserRepository users) {
        this.receipts = receipts;
        this.users = users;
    }

    public List<ReceiptResponseDto> getAllReceiptsByUsername(String username) {
        return users.findByUsername(username).map(user -> {
            List<ReceiptResponseDto> userReceipts = receipts.findByUser(user)
                .stream()
                .map(receipt -> modelMapper.map(receipt, ReceiptResponseDto.class))
                .collect(Collectors.toList());
            return userReceipts;
        }).orElseThrow(() -> new ResourceNotFoundException(String.format("Could not find user '%s'", username)));
    }

    public List<ReceiptResponseDto> getAllReceipts() {
        return receipts.findAll()
            .stream()
            .map(receipt -> modelMapper.map(receipt, ReceiptResponseDto.class))
            .collect(Collectors.toList());
    }

    public ReceiptResponseDto getReceipt(UUID uuid) {
        return receipts.findById(uuid)
            .map(receipt -> modelMapper.map(receipt, ReceiptResponseDto.class))
            .orElseThrow(() -> new ResourceNotFoundException(Receipt.class, uuid));
    }

    public Receipt addReceipt(Receipt newReceipt) {
        return receipts.save(newReceipt);
    }

    public Receipt updateReceipt(UUID uuid, Receipt newReceipt) {
        return receipts.findById(uuid).map(receipt -> {
            newReceipt.setUuid(uuid);
            return receipts.save(newReceipt);
        }).orElseThrow(() -> new ResourceNotFoundException(Receipt.class, uuid));
    }

    public void deleteReceipt(UUID uuid) {
        receipts.deleteById(uuid);
    }
}

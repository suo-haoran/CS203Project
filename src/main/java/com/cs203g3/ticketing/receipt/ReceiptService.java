package com.cs203g3.ticketing.receipt;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.receipt.dto.ReceiptRequestDto;
import com.cs203g3.ticketing.receipt.dto.ReceiptResponseDto;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.user.UserRepository;

@Service
public class ReceiptService {
    
    private ModelMapper modelMapper;

    private ReceiptRepository receipts;
    private UserRepository users;

    public ReceiptService(ModelMapper modelMapper, ReceiptRepository receipts, UserRepository users) {
        this.modelMapper = modelMapper;

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
        return receipts.findAll().stream()
            .map(receipt -> modelMapper.map(receipt, ReceiptResponseDto.class))
            .collect(Collectors.toList());
    }

    public ReceiptResponseDto getReceipt(UUID uuid) {
        return receipts.findById(uuid)
            .map(receipt -> modelMapper.map(receipt, ReceiptResponseDto.class))
            .orElseThrow(() -> new ResourceNotFoundException(Receipt.class, uuid));
    }

    public Receipt addReceipt(ReceiptRequestDto newReceiptDto) {
        Long userId = newReceiptDto.getUserId();
        User user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

        Receipt newReceipt = modelMapper.map(newReceiptDto, Receipt.class);
        newReceipt.setUser(user);
        return receipts.save(newReceipt);
    }

    public ReceiptResponseDto addReceiptAsUserId(Long userId, ReceiptRequestDto newReceiptDto) {
        newReceiptDto.setUserId(userId);
        Receipt newReceipt = this.addReceipt(newReceiptDto);

        return modelMapper.map(newReceipt, ReceiptResponseDto.class);
    }

    public ReceiptResponseDto updateReceipt(UUID uuid, ReceiptRequestDto newReceiptDto) {
        receipts.findById(uuid).orElseThrow(() -> new ResourceNotFoundException(Receipt.class, uuid));
        Long userId = newReceiptDto.getUserId();
        User user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

        Receipt newReceipt = modelMapper.map(newReceiptDto, Receipt.class);
        newReceipt.setUuid(uuid);
        newReceipt.setUser(user);
        receipts.save(newReceipt);

        return modelMapper.map(newReceipt, ReceiptResponseDto.class);
    }

    public void deleteReceipt(UUID uuid) {
        receipts.deleteById(uuid);
    }
}

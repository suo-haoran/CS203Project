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

    /**
     * Retrieves all receipts for a given username.
     *
     * @param username The username for which to retrieve receipts.
     * @return A list of receipt response DTOs for the user.
     * @throws ResourceNotFoundException If the user with the specified username is
     *                                   not found.
     */
    public List<ReceiptResponseDto> getAllReceiptsByUsername(String username) {
        return users.findByUsername(username).map(user -> {
            List<ReceiptResponseDto> userReceipts = receipts.findByUser(user)
                    .stream()
                    .map(receipt -> modelMapper.map(receipt, ReceiptResponseDto.class))
                    .collect(Collectors.toList());
            return userReceipts;
        }).orElseThrow(() -> new ResourceNotFoundException(String.format("Could not find user '%s'", username)));
    }

    /**
     * Retrieves all receipts in the system.
     *
     * @return A list of receipt response DTOs for all receipts.
     */
    public List<ReceiptResponseDto> getAllReceipts() {
        return receipts.findAll().stream()
                .map(receipt -> modelMapper.map(receipt, ReceiptResponseDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a receipt by its UUID.
     *
     * @param uuid The UUID of the receipt to retrieve.
     * @return The receipt response DTO.
     * @throws ResourceNotFoundException If the receipt with the specified UUID is
     *                                   not found.
     */
    public ReceiptResponseDto getReceipt(UUID uuid) {
        return receipts.findById(uuid)
                .map(receipt -> modelMapper.map(receipt, ReceiptResponseDto.class))
                .orElseThrow(() -> new ResourceNotFoundException(Receipt.class, uuid));
    }

    /**
     * Adds a new receipt to the system.
     *
     * @param newReceiptDto The receipt request DTO containing receipt information.
     * @return The created receipt entity.
     * @throws ResourceNotFoundException If the user with the specified ID is not
     *                                   found.
     */
    public Receipt addReceipt(ReceiptRequestDto newReceiptDto) {
        Long userId = newReceiptDto.getUserId();
        User user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

        Receipt newReceipt = modelMapper.map(newReceiptDto, Receipt.class);
        newReceipt.setUser(user);
        return receipts.save(newReceipt);
    }

    /**
     * Adds a new receipt to the system with the specified user ID.
     *
     * @param userId        The ID of the user for whom to add the receipt.
     * @param newReceiptDto The receipt request DTO containing receipt information.
     * @return The created receipt response DTO.
     * @throws ResourceNotFoundException If the user with the specified ID is not
     *                                   found.
     */

    public ReceiptResponseDto addReceiptAsUserId(Long userId, ReceiptRequestDto newReceiptDto) {
        newReceiptDto.setUserId(userId);
        Receipt newReceipt = this.addReceipt(newReceiptDto);

        return modelMapper.map(newReceipt, ReceiptResponseDto.class);
    }

    /**
     * Updates an existing receipt in the system.
     *
     * @param uuid          The UUID of the receipt to update.
     * @param newReceiptDto The receipt request DTO containing updated information.
     * @return The updated receipt response DTO.
     * @throws ResourceNotFoundException If the receipt with the specified UUID is
     *                                   not found.
     * @throws ResourceNotFoundException If the user with the specified ID is not
     *                                   found.
     */
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

    /**
     * Deletes a receipt by its UUID.
     *
     * @param uuid The UUID of the receipt to delete.
     */
    public void deleteReceipt(UUID uuid) {
        receipts.deleteById(uuid);
    }
}

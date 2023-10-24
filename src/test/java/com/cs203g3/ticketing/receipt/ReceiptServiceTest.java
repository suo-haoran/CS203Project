package com.cs203g3.ticketing.receipt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.receipt.dto.ReceiptRequestDto;
import com.cs203g3.ticketing.receipt.dto.ReceiptResponseDto;
import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.user.UserRepository;

import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReceiptServiceTest {

    @InjectMocks
    private ReceiptService receiptService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private UserRepository userRepository;

    private final User USER = new User(
            1L, "testuser1", "", "ticketingwinners@gmail.com", "12312312", "SG", new Date(), null);


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }    

    @Test
    public void getAllReceiptsByUsername_Success() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(USER));

        List<Ticket> mockTickets = List.of(new Ticket(), new Ticket());
        List<Receipt> mockReceipts = Arrays.asList(
            new Receipt(UUID.randomUUID(), USER, new BigDecimal("100.50"), mockTickets),
            new Receipt(UUID.randomUUID(), USER, new BigDecimal("75.25"), mockTickets)
        );
        when(receiptRepository.findByUser(USER)).thenReturn(mockReceipts);

        List<ReceiptResponseDto> result = receiptService.getAllReceiptsByUsername(username);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findByUsername(username);
        verify(receiptRepository).findByUser(USER);
    }

    @Test
    public void getAllReceiptsByUsername_UserNotFound_Failure() {
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> receiptService.getAllReceiptsByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    public void getAllReceipts_Success() {
        List<Ticket> mockTickets = List.of(new Ticket(), new Ticket());
        List<Receipt> mockReceipts = Arrays.asList(
            new Receipt(UUID.randomUUID(), USER, new BigDecimal("100.50"), mockTickets),
            new Receipt(UUID.randomUUID(), USER, new BigDecimal("75.25"), mockTickets)
        );
        when(receiptRepository.findAll()).thenReturn(mockReceipts);

        List<ReceiptResponseDto> result = receiptService.getAllReceipts();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(receiptRepository).findAll();
    }

    @Test
    public void getReceipt_Success() {
        UUID receiptUuid = UUID.randomUUID();
        Receipt mockReceipt =  new Receipt(UUID.randomUUID(), USER, new BigDecimal("100.50"), new ArrayList<>());
        when(receiptRepository.findById(receiptUuid)).thenReturn(Optional.of(mockReceipt));
        when(modelMapper.map(any(Receipt.class), eq(ReceiptResponseDto.class))).thenReturn(new ReceiptResponseDto());
        ReceiptResponseDto result = receiptService.getReceipt(receiptUuid);

        assertNotNull(result);
        verify(receiptRepository).findById(receiptUuid);
    }

    @Test
    public void getReceipt_NotFound_Failure() {
        UUID nonExistentUuid = UUID.randomUUID();
        when(receiptRepository.findById(nonExistentUuid)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> receiptService.getReceipt(nonExistentUuid));
        verify(receiptRepository).findById(nonExistentUuid);
    }

    @Test
    public void addReceipt_Success() {
        ReceiptRequestDto receiptRequestDto = new ReceiptRequestDto(1L, new BigDecimal("75.50"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(USER));

        Receipt mockReceipt = new Receipt(UUID.randomUUID(), USER, new BigDecimal("75.50"), new ArrayList<>());
        when(modelMapper.map(receiptRequestDto, Receipt.class)).thenReturn(mockReceipt);

        when(receiptRepository.save(any(Receipt.class))).thenReturn(mockReceipt);

        Receipt addedReceipt = receiptService.addReceipt(receiptRequestDto);

        assertNotNull(addedReceipt);
        verify(userRepository).findById(1L);
        verify(receiptRepository).save(any(Receipt.class));
    }

    @Test
    public void addReceipt_UserNotFound_Failure() {
        ReceiptRequestDto receiptRequestDto = new ReceiptRequestDto(1L, new BigDecimal("50.25"));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> receiptService.addReceipt(receiptRequestDto));
        verify(userRepository).findById(1L);
    }

    @Test
    public void addReceiptAsUserId_Success() {
        Long userId = 1L;
        ReceiptRequestDto receiptRequestDto = new ReceiptRequestDto(userId, new BigDecimal("45.75"));
   
        when(userRepository.findById(userId)).thenReturn(Optional.of(USER));

        Receipt mockReceipt = new Receipt(UUID.randomUUID(), USER, new BigDecimal("45.75"), new ArrayList<>());
        when(modelMapper.map(receiptRequestDto, Receipt.class)).thenReturn(mockReceipt);

        when(receiptRepository.save(any(Receipt.class))).thenReturn(mockReceipt);
        when(modelMapper.map(mockReceipt, ReceiptResponseDto.class)).thenReturn(new ReceiptResponseDto());

        ReceiptResponseDto result = receiptService.addReceiptAsUserId(userId, receiptRequestDto);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(modelMapper).map(receiptRequestDto, Receipt.class);
        verify(receiptRepository).save(any(Receipt.class));
        verify(modelMapper).map(mockReceipt, ReceiptResponseDto.class);

    }

    @Test
    public void updateReceipt_Success() {
        UUID receiptUuid = UUID.randomUUID();
        ReceiptRequestDto receiptRequestDto = new ReceiptRequestDto(1L, new BigDecimal("60.00"));

        Receipt existingReceipt = new Receipt(UUID.randomUUID(), USER, new BigDecimal("50.75"), new ArrayList<>());
        when(receiptRepository.findById(receiptUuid)).thenReturn(Optional.of(existingReceipt));
        when(userRepository.findById(1L)).thenReturn(Optional.of(USER));

        Receipt updatedReceipt = new Receipt(receiptUuid, USER, new BigDecimal("60.00"), new ArrayList<>());
        when(modelMapper.map(receiptRequestDto, Receipt.class)).thenReturn(updatedReceipt);

        when(receiptRepository.save(any(Receipt.class))).thenReturn(updatedReceipt);
        ReceiptResponseDto mockResponse = new ReceiptResponseDto();
        mockResponse.setAmountPaid(new BigDecimal("60.00"));
        when(modelMapper.map(updatedReceipt, ReceiptResponseDto.class)).thenReturn(mockResponse);

        ReceiptResponseDto result = receiptService.updateReceipt(receiptUuid, receiptRequestDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("60.00"), result.getAmountPaid());
    }

    @Test
    public void updateReceipt_ReceiptNotFound_Failure() {
        UUID nonExistentUuid = UUID.randomUUID();
        ReceiptRequestDto receiptRequestDto = new ReceiptRequestDto(1L, new BigDecimal("65.00"));

        when(receiptRepository.findById(nonExistentUuid)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> receiptService.updateReceipt(nonExistentUuid, receiptRequestDto));
        verify(receiptRepository).findById(nonExistentUuid);
    }

    @Test
    public void updateReceipt_UserNotFound_Failure() {
        UUID receiptUuid = UUID.randomUUID();
        ReceiptRequestDto receiptRequestDto = new ReceiptRequestDto(1L, new BigDecimal("70.00"));
        when(receiptRepository.findById(receiptUuid)).thenReturn(Optional.of(new Receipt()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> receiptService.updateReceipt(receiptUuid, receiptRequestDto));
        verify(receiptRepository).findById(receiptUuid);
        verify(userRepository).findById(1L);
    }

    @Test
    public void deleteReceipt_Success() {
        UUID receiptUuid = UUID.randomUUID();

        doNothing().when(receiptRepository).deleteById(receiptUuid);

        assertDoesNotThrow(() -> receiptService.deleteReceipt(receiptUuid));
        verify(receiptRepository).deleteById(receiptUuid);
    }
}

package com.cs203g3.ticketing.ticket;

import java.util.UUID;

import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException.NotFound;

import com.cs203g3.exception.ResourceNotFoundException;

@Service
public class TicketService {
    
    private TicketRepository ticketRepository;
    private TicketCategoryRepository ticketCategoryRepository;

    public TicketService(TicketRepository ticketRepository, TicketCategoryRepository ticketCategoryRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketCategoryRepository = ticketCategoryRepository;
    }

    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public TicketCategory createTicketCategory(TicketCategory ticketCategory) {
        return ticketCategoryRepository.save(ticketCategory);
    }

    public Ticket getTicketById(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Could not find Ticket with ID #" + id));
    }

    public TicketCategory getTicketCategoryById(Long id) {
        return ticketCategoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(TicketCategory.class, id));
    }

    public Ticket updateTicket(Ticket ticket) {
        if (!ticketRepository.existsById(ticket.getId())) {
            throw new ResourceNotFoundException("Could not find Ticket with ID #" + ticket.getId());
        }
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Ticket ticket) {
        ticketRepository.delete(ticket);
    }
}

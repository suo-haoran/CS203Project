package com.cs203g3.ticketing;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.receipt.dto.ReceiptResponseDto;

@SpringBootApplication
@EnableJpaRepositories
public class TicketingApplication {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(Receipt.class, ReceiptResponseDto.class)
			.addMapping(src -> src.getUser().getUsername(), ReceiptResponseDto::setUsername);
		return modelMapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(TicketingApplication.class, args);
	}

}

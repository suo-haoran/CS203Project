package com.cs203g3.ticketing;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.receipt.dto.ReceiptResponseDto;
import com.cs203g3.ticketing.section.Section;
import com.cs203g3.ticketing.section.dto.SectionResponseDto;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
public class TicketingApplication {

    @Bean
    ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		modelMapper.typeMap(Receipt.class, ReceiptResponseDto.class)
			.addMapping(src -> src.getUser().getUsername(), ReceiptResponseDto::setUsername);

		modelMapper.typeMap(Section.class, SectionResponseDto.class)
			.addMapping(src -> src.getCategory().getName(), SectionResponseDto::setCategory);

		return modelMapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(TicketingApplication.class, args);
	}

}

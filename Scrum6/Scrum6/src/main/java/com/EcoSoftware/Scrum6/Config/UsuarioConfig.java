package com.EcoSoftware.Scrum6.Config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsuarioConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

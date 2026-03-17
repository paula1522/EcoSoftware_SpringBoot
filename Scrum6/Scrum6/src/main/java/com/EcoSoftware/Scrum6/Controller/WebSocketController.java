package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.WebSocketDTO;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller

public class WebSocketController {
    @MessageMapping("/chat/{salaId}")
    @SendTo("/topic{saleId}")
    public WebSocketDTO chat(@DestinationVariable String salaId, WebSocketDTO message){
        return new WebSocketDTO(message.getMessage(), message.getUser());
    }
}

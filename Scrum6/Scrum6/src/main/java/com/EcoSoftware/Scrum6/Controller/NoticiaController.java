package com.EcoSoftware.Scrum6.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EcoSoftware.Scrum6.DTO.NoticiaDTO;
import com.EcoSoftware.Scrum6.Service.NoticiaService;

@RestController
@RequestMapping("/api/noticias")
@CrossOrigin(origins = "http://localhost:4200")
public class NoticiaController {

    @Autowired
    private NoticiaService noticiaService;

    @GetMapping
    public List<NoticiaDTO> listarNoticias() {
        return noticiaService.obtenerNoticias();
    }
}

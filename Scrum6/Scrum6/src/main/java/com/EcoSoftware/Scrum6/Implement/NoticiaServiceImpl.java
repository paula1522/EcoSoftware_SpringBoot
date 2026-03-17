package com.EcoSoftware.Scrum6.Implement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.EcoSoftware.Scrum6.DTO.NoticiaDTO;
import com.EcoSoftware.Scrum6.Service.NoticiaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NoticiaServiceImpl implements NoticiaService {

    @Value("${gnews.api.key}")
    private String apiKey;

    @Value("${gnews.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<NoticiaDTO> obtenerNoticias() {

        String url = apiUrl
                + "?q=reciclaje OR ecologia OR ambiente OR sostenible OR residuos"
                + "&lang=es"
                + "&max=20"
                + "&token=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> articles = (List<Map<String, Object>>) response.get("articles");

        List<NoticiaDTO> noticias = new ArrayList<>();

        if (articles != null) {
            for (Map<String, Object> item : articles) {

                Map<String, Object> fuente = (Map<String, Object>) item.get("source");

                NoticiaDTO dto = new NoticiaDTO();
                dto.setTitulo((String) item.get("title"));
                dto.setDescripcion((String) item.get("description"));
                dto.setUrl((String) item.get("url"));
                dto.setImagen((String) item.get("image"));
                dto.setFecha((String) item.get("publishedAt"));
                dto.setFuente(fuente != null ? (String) fuente.get("name") : "Desconocido");

                noticias.add(dto);
            }
        }

        return noticias;
    }
}

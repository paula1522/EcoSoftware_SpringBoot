package com.EcoSoftware.Scrum6.Implement.DatosPrecargados;

import com.EcoSoftware.Scrum6.Entity.PuntoReciclaje;
import com.EcoSoftware.Scrum6.Repository.PuntoRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class CargarPuntosReciclaje implements CommandLineRunner {

    private final PuntoRepository puntoRepository;

    public CargarPuntosReciclaje(PuntoRepository puntoRepository) {
        this.puntoRepository = puntoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        int creados = 0;
        int actualizados = 0;

        actualizados += crearOActualizar(
                "Punto Ecologico Monserrate",
                "Avenida Circunvalar con Calle 26, Bogota",
                "Lunes a Domingo 8:00 AM - 5:00 PM",
                "Papel, Plastico, Vidrio, Metal",
                "Punto de reciclaje cercano al acceso de Monserrate con alta afluencia de visitantes.",
                4.6056,
                -74.0558);

        actualizados += crearOActualizar(
                "EcoPunto Parque de la 93",
                "Calle 93A #11A-11, Bogota",
                "Lunes a Domingo 7:00 AM - 6:00 PM",
                "Papel, Plastico, Vidrio",
                "Punto de reciclaje en una de las zonas mas visitadas del norte de Bogota.",
                4.6761,
                -74.0487);

        actualizados += crearOActualizar(
                "Punto Verde Usaquen",
                "Carrera 6A #118-60, Bogota",
                "Lunes a Sabado 8:00 AM - 5:00 PM",
                "Papel, Vidrio, Metal",
                "Punto de apoyo ambiental cercano al mercado de pulgas de Usaquen.",
                4.6956,
                -74.0300);

        actualizados += crearOActualizar(
                "EcoPunto Unicentro",
                "Carrera 15 #124-30, Bogota",
                "Lunes a Domingo 9:00 AM - 6:00 PM",
                "Papel, Plastico, Metal",
                "Centro de recoleccion cercano a Unicentro para residuos aprovechables.",
                4.7025,
                -74.0402);

        actualizados += crearOActualizar(
                "Recicla Salitre",
                "Avenida La Esperanza #68B-45, Bogota",
                "Lunes a Viernes 8:00 AM - 5:00 PM",
                "Papel, Plastico, Metal",
                "Punto de reciclaje de facil acceso en el sector de Salitre.",
                4.6557,
                -74.1089);

        actualizados += crearOActualizar(
                "Punto Verde Simon Bolivar",
                "Avenida Calle 53 con Carrera 66A, Bogota",
                "Lunes a Domingo 7:00 AM - 4:00 PM",
                "Papel, Vidrio, Metal",
                "Punto ambiental junto al Parque Simon Bolivar para visitantes y deportistas.",
                4.6581,
                -74.0939);

        actualizados += crearOActualizar(
                "EcoPunto Movistar Arena",
                "Diagonal 61C #26-36, Bogota",
                "Lunes a Sabado 8:00 AM - 5:00 PM",
                "Papel, Plastico, Metal",
                "Punto de separacion de residuos en el entorno del Movistar Arena.",
                4.6485,
                -74.0777);

        actualizados += crearOActualizar(
                "Punto Recicla Museo Nacional",
                "Carrera 7 #28-66, Bogota",
                "Lunes a Viernes 8:00 AM - 4:00 PM",
                "Papel, Vidrio, Plastico",
                "Punto ubicado en el corredor cultural del centro internacional.",
                4.6153,
                -74.0673);

        actualizados += crearOActualizar(
                "EcoPunto Plaza de Bolivar",
                "Carrera 7 #11-10, Bogota",
                "Lunes a Domingo 8:00 AM - 5:00 PM",
                "Papel, Plastico, Metal",
                "Punto iconico de reciclaje en el centro historico de Bogota.",
                4.5981,
                -74.0758);

        actualizados += crearOActualizar(
                "Punto Verde La Candelaria",
                "Calle 12B #2-98, Bogota",
                "Martes a Domingo 9:00 AM - 5:00 PM",
                "Papel, Vidrio, Metal",
                "Punto de acopio para el sector turistico y cultural de La Candelaria.",
                4.5968,
                -74.0727);

        actualizados += crearOActualizar(
                "EcoPunto Museo del Oro",
                "Carrera 6 #15-88, Bogota",
                "Lunes a Viernes 8:00 AM - 5:00 PM",
                "Papel, Plastico, Metal",
                "Punto ambiental cercano al Museo del Oro y la Avenida Jimenez.",
                4.6019,
                -74.0724);

        actualizados += crearOActualizar(
                "Punto Limpio Zona T",
                "Carrera 13 #82-36, Bogota",
                "Lunes a Domingo 10:00 AM - 7:00 PM",
                "Plastico, Vidrio, Metal",
                "Punto de reciclaje en el corredor comercial y gastronomico de Zona T.",
                4.6683,
                -74.0554);

        actualizados += crearOActualizar(
                "EcoPunto Andino",
                "Carrera 11 #82-71, Bogota",
                "Lunes a Domingo 9:00 AM - 6:00 PM",
                "Papel, Plastico, Metal",
                "Punto de reciclaje de facil acceso para visitantes del centro comercial Andino.",
                4.6672,
                -74.0539);

        actualizados += crearOActualizar(
                "Punto Verde Lourdes",
                "Carrera 13 #63-27, Bogota",
                "Lunes a Sabado 8:00 AM - 5:00 PM",
                "Papel, Vidrio, Metal",
                "Punto barrial de reciclaje cerca de la Plaza de Lourdes.",
                4.6481,
                -74.0637);

        actualizados += crearOActualizar(
                "EcoPunto Campin",
                "Carrera 30 #57-50, Bogota",
                "Lunes a Domingo 8:00 AM - 5:00 PM",
                "Plastico, Vidrio, Metal",
                "Punto de reciclaje cercano al estadio El Campin y su zona deportiva.",
                4.6459,
                -74.0779);

        actualizados += crearOActualizar(
                "Punto Verde Corferias",
                "Carrera 37 #24-67, Bogota",
                "Lunes a Viernes 8:00 AM - 5:00 PM",
                "Papel, Plastico, Metal",
                "Punto de recoleccion para el sector ferial y empresarial de Corferias.",
                4.6295,
                -74.0895);

        actualizados += crearOActualizar(
                "EcoPunto Terminal Salitre",
                "Diagonal 23 #69-60, Bogota",
                "Lunes a Domingo 7:00 AM - 6:00 PM",
                "Papel, Plastico, Metal",
                "Punto de reciclaje para viajeros y comerciantes de la terminal.",
                4.6523,
                -74.1152);

        actualizados += crearOActualizar(
                "Punto Verde Jardin Botanico",
                "Avenida Calle 63 #68-95, Bogota",
                "Martes a Domingo 8:00 AM - 4:00 PM",
                "Papel, Plastico, Vidrio",
                "Punto ambiental con enfoque educativo en el entorno del Jardin Botanico.",
                4.6670,
                -74.1029);

        actualizados += crearOActualizar(
                "EcoPunto Parque Nacional",
                "Carrera 7 #39-50, Bogota",
                "Lunes a Domingo 7:00 AM - 5:00 PM",
                "Papel, Vidrio, Metal",
                "Punto de reciclaje para visitantes del Parque Nacional.",
                4.6219,
                -74.0651);

        actualizados += crearOActualizar(
                "Punto Recicla Usaquen Hacienda Santa Barbara",
                "Carrera 7 #115-60, Bogota",
                "Lunes a Domingo 9:00 AM - 6:00 PM",
                "Papel, Plastico, Vidrio, Metal",
                "Punto de reciclaje en una zona comercial reconocida del norte de Bogota.",
                4.6974,
                -74.0317);

        creados = Math.max(0, actualizados);
        System.out.println(">>> Puntos de reciclaje sincronizados. Registros creados o actualizados: " + actualizados);
    }

    private int crearOActualizar(
            String nombre,
            String direccion,
            String horario,
            String tipoResiduo,
            String descripcion,
            Double latitud,
            Double longitud) {

                PuntoReciclaje punto = puntoRepository
                                .findByNombreIgnoreCaseAndDireccionIgnoreCase(nombre, direccion)
                                .orElseGet(PuntoReciclaje::new);

        punto.setNombre(nombre);
        punto.setDireccion(direccion);
        punto.setHorario(horario);
        punto.setTipoResiduo(tipoResiduo);
        punto.setDescripcion(descripcion);
        punto.setLatitud(latitud);
        punto.setLongitud(longitud);
        punto.setUsuarioId(null);
        punto.setImagen(null);

        puntoRepository.save(punto);
        return 1;
    }
}
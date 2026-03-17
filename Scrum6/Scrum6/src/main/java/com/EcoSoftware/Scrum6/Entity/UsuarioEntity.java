package com.EcoSoftware.Scrum6.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import com.EcoSoftware.Scrum6.Enums.EstadoRegistro;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    // --- RELACIÓN CON ROL ---
    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private RolEntity rol;

    // --- DATOS BÁSICOS ---
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false, unique = true)
    private String cedula;

    @Column(nullable = false)
    private String telefono;

    @Column
    private String direccion;

    @Column
    private String barrio;

    @Column
    private String localidad;

    // --- EMPRESAS ---
    @Column
    private String nit;

    @Column
    private String representanteLegal;

    @Column(columnDefinition = "TEXT")
    private String Rut; // Documento RUT de empresa
    @Column(columnDefinition = "TEXT")
private String camara_comercio;

    // --- RECICLADORES / EMPRESAS ---
    @Column
    private String zona_de_trabajo;

    @Column
    private String horario;

    @Column
    private String tipoMaterial;

    @Column
    private Integer cantidad_minima;

    // --- ARCHIVOS / IMÁGENES ---
    @Column
    private String imagen_perfil; // URL imagen

    @Column(columnDefinition = "TEXT")
    private String certificaciones; // Certificados reciclador

    @Column(columnDefinition = "TEXT")
    private String Documento; // Documento reciclador o representante legal

    // --- ESTADO ---
    @Column(nullable = false)
    private Boolean estado = true; // Admin y ciudadano activos por defecto

    // --- FECHAS ---
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @Enumerated(EnumType.STRING)
@Column(name = "estado_registro")
private EstadoRegistro estadoRegistro;

    // --- EVENTOS AUTOMÁTICOS ---
    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}

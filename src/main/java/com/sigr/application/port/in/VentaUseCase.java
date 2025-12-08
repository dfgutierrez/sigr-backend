package com.sigr.application.port.in;

import com.sigr.application.dto.venta.VentaRequestDTO;
import com.sigr.application.dto.venta.VentaResponseDTO;
import com.sigr.application.dto.venta.VentaDescripcionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaUseCase {
    
    VentaResponseDTO crearVenta(VentaRequestDTO ventaRequestDTO);
    
    VentaResponseDTO obtenerVentaPorId(Long id);
    
    Page<VentaResponseDTO> obtenerTodasLasVentas(Pageable pageable);
    
    Page<VentaResponseDTO> obtenerVentasPorSede(Long sedeId, Pageable pageable);
    
    Page<VentaResponseDTO> obtenerVentasPorSedeYFecha(Long sedeId, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
    
    Page<VentaResponseDTO> obtenerVentasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);
    
    Page<VentaResponseDTO> obtenerVentasPorUsuario(Long usuarioId, Pageable pageable);
    
    // Método eliminado - no existe concepto de cliente
    
    void anularVenta(Long id);
    
    List<VentaResponseDTO> obtenerVentasDelDia(Long sedeId);
    
    Page<VentaResponseDTO> obtenerVentasPendientesPorEntregar(Pageable pageable);
    
    Page<VentaResponseDTO> obtenerVentasPendientesPorEntregarPorSede(Long sedeId, Pageable pageable);
    
    VentaResponseDTO actualizarFechaEntrega(Long id, LocalDateTime nuevaFechaEntrega);
    
    VentaResponseDTO actualizarDescripcion(Long id, VentaDescripcionDTO descripcionDTO);
}
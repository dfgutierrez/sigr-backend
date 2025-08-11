package com.sigr.application.dto.dashboard;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DashboardResponseDTO {
    
    private List<ProductosVendidosPorSedeDTO> productosVendidosMesActual;
    private List<ProductosVendidosPorSedeDTO> productosVendidosDiaActual;
    private Long vehiculosNuevosDiaActual;
    private List<ProductosPorSedeDTO> productosPorSede;
    private List<VentaMensualDTO> ventasMensuales;
    private List<ProductoMasVendidoDTO> productosMasVendidos;
    private KpisDTO kpis;
    private LocalDateTime fecha;
    
    @Data
    public static class ProductosVendidosPorSedeDTO {
        private Long sedeId;
        private String sedeNombre;
        private Long cantidadProductosVendidos;
        
        public ProductosVendidosPorSedeDTO() {}
        
        public ProductosVendidosPorSedeDTO(Long sedeId, String sedeNombre, Long cantidadProductosVendidos) {
            this.sedeId = sedeId;
            this.sedeNombre = sedeNombre;
            this.cantidadProductosVendidos = cantidadProductosVendidos;
        }
    }
    
    @Data
    public static class ProductosPorSedeDTO {
        private Long sedeId;
        private String sedeNombre;
        private Long cantidadProductos;
        
        public ProductosPorSedeDTO() {}
        
        public ProductosPorSedeDTO(Long sedeId, String sedeNombre, Long cantidadProductos) {
            this.sedeId = sedeId;
            this.sedeNombre = sedeNombre;
            this.cantidadProductos = cantidadProductos;
        }
    }
    
    @Data
    public static class VentaMensualDTO {
        private String mes;
        private BigDecimal value;
        private Integer month;
        private Integer year;
        
        public VentaMensualDTO() {}
        
        public VentaMensualDTO(String mes, BigDecimal value, Integer month, Integer year) {
            this.mes = mes;
            this.value = value;
            this.month = month;
            this.year = year;
        }
    }
    
    @Data
    public static class ProductoMasVendidoDTO {
        private String nombre;
        private Long cantidadVendida;
        private Long productoId;
        
        public ProductoMasVendidoDTO() {}
        
        public ProductoMasVendidoDTO(String nombre, Long cantidadVendida, Long productoId) {
            this.nombre = nombre;
            this.cantidadVendida = cantidadVendida;
            this.productoId = productoId;
        }
    }
    
    @Data
    public static class KpisDTO {
        private VentaMesDTO ventasMesActual;
        private VentaMesDTO ventasMesAnterior;
        private InventarioDTO inventario;
        
        @Data
        public static class VentaMesDTO {
            private BigDecimal total;
            private Long cantidad;
            
            public VentaMesDTO() {}
            
            public VentaMesDTO(BigDecimal total, Long cantidad) {
                this.total = total;
                this.cantidad = cantidad;
            }
        }
        
        @Data
        public static class InventarioDTO {
            private Long productosEnStock;
            private Long productosTotal;
            
            public InventarioDTO() {}
            
            public InventarioDTO(Long productosEnStock, Long productosTotal) {
                this.productosEnStock = productosEnStock;
                this.productosTotal = productosTotal;
            }
        }
    }
}
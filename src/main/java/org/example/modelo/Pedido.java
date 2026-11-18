package org.example.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private Integer idPedido;
    private LocalDate fechaPedido;
    private Integer cantidad;
    private Integer idProducto;
    private String nombreProducto;      //Uso esta instrucción para almacenar el nombre del producto cada vez que se hace un JOIN
}
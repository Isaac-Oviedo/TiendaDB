package org.example;

import org.example.dao.PedidoDAO;
import org.example.dao.ProductoDAO;
import org.example.modelo.Pedido;
import org.example.modelo.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductoDAO productoDAO = new ProductoDAO();
    private static final PedidoDAO pedidoDAO = new PedidoDAO();

    public static void main(String[] args) {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");
            try {
                switch (opcion) {
                    case 1 -> insertarProducto();
                    case 2 -> consultarProducto();
                    case 3 -> actualizarStock();
                    case 4 -> insertarPedido();
                    case 5 -> listarPedidos();
                    case 6 -> System.out.println("Hasta luego.");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            }
        } while (opcion != 6);
    }

    private static void mostrarMenu() {
        System.out.println("\n--- TIENDA ---");
        System.out.println("1. Insertar producto");
        System.out.println("2. Consultar producto por id");
        System.out.println("3. Actualizar stock de producto");
        System.out.println("4. Insertar pedido");
        System.out.println("5. Listado de pedidos");
        System.out.println("6. Salir");
    }

    private static void insertarProducto() throws SQLException {
        String nombre = leerTexto("Nombre: ");
        BigDecimal precio = leerDecimal("Precio: ");
        int stock = leerEntero("Stock: ");
        Producto producto = new Producto(null, nombre, precio, stock);
        productoDAO.insertar(producto);
        System.out.println("Producto insertado con id " + producto.getIdProducto());
    }

    private static void consultarProducto() throws SQLException {
        int id = leerEntero("Id del producto: ");
        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null) {
            System.out.println("El producto con id " + id + " no existe.");
        } else {
            System.out.println(producto);
        }
    }

    private static void actualizarStock() throws SQLException {
        int id = leerEntero("Id del producto: ");
        int nuevoStock = leerEntero("Nuevo stock: ");
        if (productoDAO.actualizarStock(id, nuevoStock)) {
            System.out.println("Stock actualizado.");
        } else {
            System.out.println("El producto con id " + id + " no existe.");
        }
    }

    private static void insertarPedido() throws SQLException {
        int idProducto = leerEntero("Id del producto: ");
        int cantidad = leerEntero("Cantidad: ");
        Pedido pedido = new Pedido(null, LocalDate.now(), cantidad, idProducto, null);
        try {
            pedidoDAO.insertar(pedido);
            System.out.println("Pedido insertado con id " + pedido.getIdPedido());
            Producto producto = productoDAO.buscarPorId(idProducto);
            System.out.println("Stock actual de " + producto.getNombre() + ": " + producto.getStock());
        } catch (SQLException e) {
            if (e.getErrorCode() == 1452) {
                System.out.println("El producto con id " + idProducto + " no existe.");
            } else {
                throw e;
            }
        }
    }

    private static void listarPedidos() throws SQLException {
        List<Pedido> pedidos = pedidoDAO.listar();
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos.");
            return;
        }
        for (Pedido p : pedidos) {
            System.out.printf("Pedido %d | %s | %s | cantidad: %d%n",
                    p.getIdPedido(), p.getFechaPedido(), p.getNombreProducto(), p.getCantidad());
        }
    }

    // --- Métodos auxiliares para leer del teclado ---

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número entero.");
            }
        }
    }

    private static BigDecimal leerDecimal(String mensaje) {
        while (true) {
            try {
                return new BigDecimal(leerTexto(mensaje).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número válido, por ejemplo 12.50.");
            }
        }
    }
}
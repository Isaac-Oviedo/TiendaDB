package org.example.dao;

import org.example.modelo.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    // Inserta el pedido Y resta el stock, como una sola operación
    public void insertar(Pedido pedido) throws SQLException {
        String sqlPedido = "INSERT INTO pedidos (fecha_pedido, cantidad, id_producto) VALUES (?, ?, ?)";
        String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);   // iniciamos una transacción
            try (PreparedStatement stmtPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement stmtStock = con.prepareStatement(sqlStock)) {

                // Sentencia 1: insertar el pedido
                stmtPedido.setDate(1, Date.valueOf(pedido.getFechaPedido()));
                stmtPedido.setInt(2, pedido.getCantidad());
                stmtPedido.setInt(3, pedido.getIdProducto());
                stmtPedido.executeUpdate();
                try (ResultSet rs = stmtPedido.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedido.setIdPedido(rs.getInt(1));
                    }
                }

                // Sentencia 2: reducir el stock del producto
                stmtStock.setInt(1, pedido.getCantidad());
                stmtStock.setInt(2, pedido.getIdProducto());
                stmtStock.executeUpdate();

                con.commit();           // las dos sentencias han ido bien
            } catch (SQLException e) {
                con.rollback();         // algo falló: deshacemos todo
                throw e;
            }
        }
    }

    // Lista todos los pedidos con el nombre del producto (JOIN)
    public List<Pedido> listar() throws SQLException {
        String sql = "SELECT p.id_pedido, p.fecha_pedido, p.cantidad, p.id_producto, pr.nombre "
                + "FROM pedidos p "
                + "JOIN productos pr ON p.id_producto = pr.id_producto "
                + "ORDER BY p.id_pedido";
        List<Pedido> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Pedido(
                        rs.getInt("id_pedido"),
                        rs.getDate("fecha_pedido").toLocalDate(),
                        rs.getInt("cantidad"),
                        rs.getInt("id_producto"),
                        rs.getString("nombre")));
            }
        }
        return lista;
    }
}
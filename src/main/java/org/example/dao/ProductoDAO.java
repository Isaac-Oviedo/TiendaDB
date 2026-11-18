package org.example.dao;

import org.example.modelo.Producto;

import java.sql.*;

public class ProductoDAO {

    // Inserta un producto y recupera el id que le asigna la BD
    public void insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, precio, stock) VALUES (?, ?, ?)";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, producto.getNombre());
            stmt.setBigDecimal(2, producto.getPrecio());
            stmt.setInt(3, producto.getStock());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    producto.setIdProducto(rs.getInt(1));
                }
            }
        }
    }

    // Devuelve el producto con ese id, o null si no existe
    public Producto buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_producto, nombre, precio, stock FROM productos WHERE id_producto = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getBigDecimal("precio"),
                            rs.getInt("stock"));
                }
            }
        }
        return null;
    }

    // Cambia solo la columna stock. Devuelve true si encontró el producto
    public boolean actualizarStock(int id, int nuevoStock) throws SQLException {
        String sql = "UPDATE productos SET stock = ? WHERE id_producto = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, nuevoStock);
            stmt.setInt(2, id);
            return stmt.executeUpdate() == 1;
        }
    }
}
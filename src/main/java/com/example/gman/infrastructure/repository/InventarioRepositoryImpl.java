package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.Inventario;
import com.example.gman.domain.repository.InventarioRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioRepositoryImpl implements InventarioRepository {

    private static final String SELECT_BASE = """
            SELECT i.id, i.codigo, i.descripcion, i.cantidad, i.precio_unitario,
                   i.proveedor_id, p.nombre AS proveedor_nombre
            FROM inventario i
            LEFT JOIN proveedores p ON p.id = i.proveedor_id
            """;

    @Override
    public List<Inventario> findAll() {
        String sql = SELECT_BASE + "ORDER BY i.descripcion";
        List<Inventario> lista = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public Inventario findById(int id) {
        String sql = SELECT_BASE + "WHERE i.id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public void save(Inventario item) {
        String sql = """
                INSERT INTO inventario (codigo, descripcion, cantidad, precio_unitario, proveedor_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getCodigo());
            ps.setString(2, item.getDescripcion());
            ps.setInt(3, item.getCantidad());
            ps.setDouble(4, item.getPrecioUnitario());
            setIntOrNull(ps, 5, item.getProveedorId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar ítem: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Inventario item) {
        String sql = """
                UPDATE inventario SET
                    codigo          = ?,
                    descripcion     = ?,
                    cantidad        = ?,
                    precio_unitario = ?,
                    proveedor_id    = ?
                WHERE id = ?
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getCodigo());
            ps.setString(2, item.getDescripcion());
            ps.setInt(3, item.getCantidad());
            ps.setDouble(4, item.getPrecioUnitario());
            setIntOrNull(ps, 5, item.getProveedorId());
            ps.setInt(6, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar ítem: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM inventario WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se puede eliminar: el ítem está vinculado a órdenes de trabajo.", e);
        }
    }

    @Override
    public boolean existsCodigo(String codigo, int excludeId) {
        String sql = "SELECT COUNT(*) FROM inventario WHERE codigo = ? AND id != ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Consumo por OT ───────────────────────────────────────────────
    /**
     * Devuelve filas: numero_ot, descripcion_item, cantidad_usada
     */
    public List<String[]> findConsumoByOT(int inventarioId) {
        String sql = """
                SELECT ot.numero_ot, i.descripcion, oi.cantidad_usada
                FROM ot_inventario oi
                JOIN orden_trabajo ot ON ot.id = oi.ot_id
                JOIN inventario     i  ON i.id  = oi.inventario_id
                WHERE oi.inventario_id = ?
                ORDER BY ot.numero_ot
                """;
        List<String[]> lista = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, inventarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(new String[]{
                        rs.getString("numero_ot"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getInt("cantidad_usada"))
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // ── Helpers ──────────────────────────────────────────────────────
    private Inventario mapear(ResultSet rs) throws SQLException {
        Inventario i = new Inventario();
        i.setId(rs.getInt("id"));
        i.setCodigo(rs.getString("codigo"));
        i.setDescripcion(rs.getString("descripcion"));
        i.setCantidad(rs.getInt("cantidad"));
        i.setPrecioUnitario(rs.getDouble("precio_unitario"));
        i.setProveedorId(rs.getInt("proveedor_id"));
        i.setProveedorNombre(rs.getString("proveedor_nombre"));
        return i;
    }

    private void setIntOrNull(PreparedStatement ps, int index, int value) throws SQLException {
        if (value > 0) ps.setInt(index, value);
        else           ps.setNull(index, Types.INTEGER);
    }
}
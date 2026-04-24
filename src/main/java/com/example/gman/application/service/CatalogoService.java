package com.example.gman.application.service;

import com.example.gman.domain.model.Catalogo;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoService {

    // ─── Obtener todos ───────────────────────────────────────────────
    public List<Catalogo> getAll() throws SQLException {
        List<Catalogo> lista = new ArrayList<>();
        String sql = "SELECT * FROM catalogo ORDER BY tipo, codigo";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Catalogo c = new Catalogo();
                c.setId(rs.getInt("id"));
                c.setTipo(rs.getString("tipo"));
                c.setCodigo(rs.getString("codigo"));
                c.setNombre(rs.getString("nombre"));
                c.setDescripcion(rs.getString("descripcion"));
                lista.add(c);
            }
        }
        return lista;
    }

    // ─── Obtener por tipo ────────────────────────────────────────────
    public List<Catalogo> getByTipo(String tipo) throws SQLException {
        List<Catalogo> lista = new ArrayList<>();
        String sql = "SELECT * FROM catalogo WHERE tipo = ? ORDER BY codigo";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Catalogo c = new Catalogo();
                    c.setId(rs.getInt("id"));
                    c.setTipo(rs.getString("tipo"));
                    c.setCodigo(rs.getString("codigo"));
                    c.setNombre(rs.getString("nombre"));
                    c.setDescripcion(rs.getString("descripcion"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }

    // ─── Crear ───────────────────────────────────────────────────────
    public void crear(Catalogo c) throws SQLException {
        String sql = "INSERT INTO catalogo (tipo, codigo, nombre, descripcion) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getTipo());
            ps.setString(2, c.getCodigo());
            ps.setString(3, c.getNombre());
            ps.setString(4, c.getDescripcion());
            ps.executeUpdate();
        }
    }

    // ─── Actualizar ──────────────────────────────────────────────────
    public void actualizar(Catalogo c) throws SQLException {
        String sql = "UPDATE catalogo SET nombre=?, descripcion=? WHERE id=?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
        }
    }

    // ─── Eliminar ────────────────────────────────────────────────────
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM catalogo WHERE id=?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
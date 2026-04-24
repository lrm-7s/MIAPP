package com.example.gman.infrastructure.network;

import com.example.gman.domain.dto.EquipoDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class ApiClient {
    private final String baseUrl;
    private final Gson gson;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.gson = new Gson();
    }

    // ── Equipos ───────────────────────────────────────────────────────────────

    public List<EquipoDTO> getEquipos() throws Exception {
        HttpURLConnection conn = openConnection(baseUrl + "/equipos", "GET");
        if (conn.getResponseCode() != 200) return Collections.emptyList();
        String body = readBody(conn);
        conn.disconnect();
        return gson.fromJson(body, new TypeToken<List<EquipoDTO>>(){}.getType());
    }

    public EquipoDTO saveEquipo(EquipoDTO dto) throws Exception {
        boolean esNuevo = dto.id == 0;
        String url    = esNuevo ? baseUrl + "/equipos" : baseUrl + "/equipos/" + dto.id;
        String method = esNuevo ? "POST" : "PUT";

        HttpURLConnection conn = openConnection(url, method);
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(gson.toJson(dto).getBytes());
        }

        int code = conn.getResponseCode();
        if (code != 200 && code != 201) {
            conn.disconnect();
            throw new RuntimeException("Error del servidor: " + code);
        }

        String body = readBody(conn);
        conn.disconnect();
        return gson.fromJson(body, EquipoDTO.class);
    }

    public boolean deleteEquipo(int id) throws Exception {
        HttpURLConnection conn = openConnection(baseUrl + "/equipos/" + id, "DELETE");
        int code = conn.getResponseCode();
        conn.disconnect();
        return code == 200 || code == 204;
    }

    public boolean testConnection() throws Exception {
        HttpURLConnection conn = openConnection(baseUrl + "/ping", "GET");
        conn.setConnectTimeout(3000);
        int code = conn.getResponseCode();
        conn.disconnect();
        return code == 200;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HttpURLConnection openConnection(String url, String method) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        return conn;
    }

    private String readBody(HttpURLConnection conn) throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}
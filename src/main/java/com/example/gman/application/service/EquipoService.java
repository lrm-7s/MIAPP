package com.example.gman.application.service;

import com.example.gman.domain.model.Equipo;
import com.example.gman.domain.repository.EquipoRepository;
import com.example.gman.infrastructure.repository.RemoteEquipoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Servicio de negocio para Equipos.
 *
 * Cambios respecto a la versión anterior:
 *  - Se agrega obtenerEquipoPorId() para soportar carga del formulario de edición.
 *  - El resto de la lógica de sync local→remoto se mantiene igual.
 */
public class EquipoService {

    private final EquipoRepository        localRepo;

    private final RemoteEquipoRepository  remoteRepo;
    private final ScheduledExecutorService executor     = Executors.newScheduledThreadPool(2);
    private final BlockingQueue<Runnable>  pendingSync  = new LinkedBlockingQueue<>();

    public EquipoService(EquipoRepository localRepo, RemoteEquipoRepository remoteRepo) {
        this.localRepo  = localRepo;
        this.remoteRepo = remoteRepo;
        startSyncWorker();
    }

    // ── Sync worker (sin cambios) ─────────────────────────────────────────

    private void startSyncWorker() {
        executor.scheduleWithFixedDelay(() -> {
            Runnable task;
            while ((task = pendingSync.poll()) != null) {
                try {
                    task.run();
                } catch (Exception e) {
                    System.err.println("[Sync] Falló tarea: " + e.getMessage());
                    pendingSync.offer(task);
                    break;
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void sincronizarGuardar(Equipo equipo) {
        if (remoteRepo == null) return;
        pendingSync.offer(() -> {
            try { remoteRepo.save(equipo); }
            catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private void sincronizarEliminar(int id) {
        if (remoteRepo == null) return;
        pendingSync.offer(() -> {
            try { remoteRepo.delete(id); }
            catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    // ── API pública ───────────────────────────────────────────────────────

    public List<Equipo> obtenerEquipos() {
        return localRepo.findAll();
    }

    /** Nuevo: carga un equipo por ID para el formulario de edición. */
    public Equipo obtenerEquipoPorId(int id) {
        return localRepo.findById(id);
    }

    public Equipo guardarEquipo(Equipo equipo) {
        Equipo guardado = localRepo.save(equipo);
        sincronizarGuardar(guardado);
        return guardado;
    }

    public void eliminarEquipo(int id) {
        localRepo.delete(id);
        sincronizarEliminar(id);
    }

    public boolean existeCodigo(String codigo, int id) {
        return localRepo.existsCodigo(codigo, id);
    }
    // EquipoService.java — método temporal hasta implementar el módulo
    public List<Equipo> listarTodos() {
        return new ArrayList<>();
    }
    public void stop() {
        // Procesar tareas restantes
        Runnable task;
        while ((task = pendingSync.poll()) != null) {
            try {
                task.run();
            } catch (Exception e) {
                System.err.println("[Shutdown] Error en tarea pendiente: " + e.getMessage());
            }
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
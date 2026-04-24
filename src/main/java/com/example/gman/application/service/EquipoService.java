package com.example.gman.application.service;

import com.example.gman.domain.model.Equipo;
import com.example.gman.domain.repository.EquipoRepository;
import com.example.gman.infrastructure.repository.RemoteEquipoRepository;

import java.util.List;
import java.util.concurrent.*;

public class EquipoService {

    private final EquipoRepository localRepo;
    private final RemoteEquipoRepository remoteRepo;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private final BlockingQueue<Runnable> pendingSync = new LinkedBlockingQueue<>();

    public EquipoService(EquipoRepository localRepo, RemoteEquipoRepository remoteRepo) {
        this.localRepo = localRepo;
        this.remoteRepo = remoteRepo;
        startSyncWorker();
    }

    private void startSyncWorker() {
        executor.scheduleWithFixedDelay(() -> {
            Runnable task;
            while ((task = pendingSync.poll()) != null) {
                try {
                    task.run();
                } catch (Exception e) {
                    System.err.println("[Sync] Falló tarea de sincronización: " + e.getMessage());
                    // Reintentar más tarde
                    pendingSync.offer(task);
                    break; // Salir y esperar próximo ciclo
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void sincronizarGuardar(Equipo equipo) {
        if (remoteRepo == null) return;
        Runnable task = () -> {
            try {
                remoteRepo.save(equipo); // enviamos Equipo directamente
            } catch (Exception e) {
                // Convertimos checked exception a unchecked para Runnable
                throw new RuntimeException(e);
            }
        };
        pendingSync.offer(task);
    }

    private void sincronizarEliminar(int id) {
        if (remoteRepo == null) return;
        Runnable task = () -> {
            try {
                remoteRepo.delete(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        pendingSync.offer(task);
    }

    public boolean existeCodigo(String codigo, int id) {
        return localRepo.existsCodigo(codigo, id);
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

    public List<Equipo> obtenerEquipos() {
        return localRepo.findAll();
    }


}
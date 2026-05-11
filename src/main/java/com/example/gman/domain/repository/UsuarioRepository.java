package com.example.gman.domain.repository;

import com.example.gman.domain.model.PermisoModulo;
import com.example.gman.domain.model.Rol;
import com.example.gman.domain.model.Usuario;

import java.util.List;

public interface UsuarioRepository {

    Usuario       findByUsername(String username)                             throws Exception;
    boolean       checkPassword(String username, String plainPassword)        throws Exception;
    void          addUser(Usuario usuario, String plainPassword)              throws Exception;
    List<Usuario> getAllUsuarios()                                             throws Exception;
    void          updateUser(String username, String nombre,
                             String password, Rol rol)                        throws Exception;
    void          deleteUser(String username)                                 throws Exception;
    void          guardarPermisos(String username,
                                  List<PermisoModulo> permisos)               throws Exception; // ← agrega throws Exception
}
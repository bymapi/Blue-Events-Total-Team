package com.example.security.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security.entities.Usuario;

public interface UsuarioRepositorio  extends JpaRepository<Usuario, Integer>{

    Optional<Usuario> findByUserName(String userName);
    boolean existsByUserName(String userName);
}

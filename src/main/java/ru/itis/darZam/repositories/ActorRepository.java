package ru.itis.darZam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.darZam.models.Actor;

import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Integer> {

    Optional<Actor> findByName(String name);
}

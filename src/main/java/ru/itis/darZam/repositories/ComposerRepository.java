package ru.itis.darZam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itis.darZam.models.Composer;

import java.util.Optional;

@Repository
public interface ComposerRepository extends JpaRepository<Composer, Integer> {

    Optional<Composer> findByName(String name);

}

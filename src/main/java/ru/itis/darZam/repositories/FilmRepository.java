package ru.itis.darZam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itis.darZam.models.Film;

@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {

}

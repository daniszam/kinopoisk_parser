package ru.itis.darZam.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import ru.itis.darZam.models.Composer;
import ru.itis.darZam.models.Film;

import java.util.Optional;

public class FilmRepositoryImpl {

    private Session session;

    public FilmRepositoryImpl(Session session){
        Configuration configuration = new Configuration().configure();
        this.session = session;

    }

    public Optional<Film> findByName(String name){
        Query query= session.
                createQuery("from Film where name=:name");
        query.setParameter("name", name);
        Film film = (Film) query.uniqueResult();
        session.close();
        return Optional.of(film);
    }

    public void save (Film film){
        Transaction transaction = session.beginTransaction();
        session.save(film);
        transaction.commit();
        session.close();

    }
}

package ru.itis.darZam.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import ru.itis.darZam.models.Film;
import ru.itis.darZam.models.RatingImdb;

import java.util.Optional;

public class RatingImdbRepositoryImpl {

    private Session session;

    public RatingImdbRepositoryImpl(Session session){
        this.session = session;
    }
    public void save (RatingImdb ratingImdb){
        Transaction transaction = session.beginTransaction();
        session.save(ratingImdb);
        transaction.commit();

    }
}

package ru.itis.darZam.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import ru.itis.darZam.models.Actor;
import ru.itis.darZam.models.Composer;

import java.util.Optional;

public class ComposerRepositoryImpl {

    private Session session;


    public ComposerRepositoryImpl(Session session){
        Configuration configuration = new Configuration().configure();
        this.session =  session;
    }

    public Optional<Composer> findByName(String name){
        Query query= session.
                createQuery("from Composer where name=:name");
        query.setParameter("name", name);
        Composer composer = (Composer) query.uniqueResult();
        session.close();
        return Optional.ofNullable(composer);
    }

    public void save (Composer composer){
        Transaction transaction = session.beginTransaction();
        session.save(composer);
        session.close();
        transaction.commit();

    }
}

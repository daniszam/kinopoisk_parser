package ru.itis.darZam.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import ru.itis.darZam.models.Actor;

import java.util.Optional;

public class ActorRepositoryImpl {

    private Session session;


    public ActorRepositoryImpl(Session session){
        Configuration configuration = new Configuration().configure();
        this.session = session;
    }

    public Optional<Actor> findByName(String name){
        Query query= session.
                createQuery("from Actor where name=:name");
        query.setParameter("name", name);
        Actor actor = (Actor) query.uniqueResult();
        return Optional.ofNullable(actor);
    }

    public void save (Actor actor){
        Transaction transaction = session.beginTransaction();
        session.save(actor);
        transaction.commit();

    }
}

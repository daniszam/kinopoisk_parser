package ru.itis.darZam.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itis.darZam.models.Actor;
import ru.itis.darZam.repositories.ActorRepository;
import ru.itis.darZam.repositories.ActorRepositoryImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ActorSerivce {


    private ActorRepositoryImpl actorRepository;

    public ActorSerivce(Session session){
        this.actorRepository =
                new ActorRepositoryImpl(session);
    }

    public Actor getOrCreate(String name){
        Optional<Actor> actor = actorRepository.findByName(name);
        if (actor.isPresent()){
            return actor.get();
        }

        Actor newActor = Actor.builder().name(name).build();
        actorRepository.save(newActor);
        return newActor;
    }

    public Set<Actor> getOrCreate(List<String> names){
        Set<Actor> actors = new HashSet<>();
        for (String name: names){
            actors.add(getOrCreate(name));
        }
        return actors;
    }
}

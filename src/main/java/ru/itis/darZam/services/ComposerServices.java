package ru.itis.darZam.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itis.darZam.models.Actor;
import ru.itis.darZam.models.Composer;
import ru.itis.darZam.repositories.ActorRepository;
import ru.itis.darZam.repositories.ComposerRepository;
import ru.itis.darZam.repositories.ComposerRepositoryImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ComposerServices {

    private ComposerRepositoryImpl composerRepository;

    public ComposerServices(Session session){
        this.composerRepository = new ComposerRepositoryImpl(session);
    }

    public Composer getOrCreate(String name){
        Optional<Composer> composer = composerRepository.findByName(name);
        if (composer.isPresent()){
            return composer.get();
        }

        Composer newComposer = Composer.builder().name(name).build();
        composerRepository.save(newComposer);
        return newComposer;
    }

    public Set<Composer> getOrCreate(List<String> names){

        Set<Composer> composers = new HashSet<>();
        for (String name: names){
            composers.add(getOrCreate(name));
        }
        return composers;
    }

}

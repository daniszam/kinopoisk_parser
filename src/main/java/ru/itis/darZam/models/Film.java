package ru.itis.darZam.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "film")
@Entity
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "film_actors",
            joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id", referencedColumnName = "id"))
    private Set<Actor> actors;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "film_composers",
            joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "composer_id", referencedColumnName = "id"))
    private Set<Composer> composers;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "film_countries",
            joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "country_id", referencedColumnName = "id"))
    private Set<Country> countries;

    @ManyToOne
    @JoinColumn
    private Director director;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "film_editors",
            joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "editor_id", referencedColumnName = "id"))
    private Set<Editor> editors;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "film_genres",
            joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "id"))
    private Set<Genre> genres;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "film_screenwriters",
            joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "screenwriter_id", referencedColumnName = "id"))
    private Set<Screenwriter> screenwriters;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "film_painters",
            joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "painter_id", referencedColumnName = "id"))
    private Set<Painter> painters;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "film_producers",
            joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "producer_id", referencedColumnName = "id"))
    private Set<Producer> producers;

    @ManyToOne
    @JoinColumn
    private Operator operator;

    private int year;

    private String tagline;

    private Float budget;

    private Float dues;

    private Date premiere;

    private Short age;

    private Float duration;

    @OneToOne
    @JoinColumn(name = "rating_kinopoisk_id")
    private RatingKinopoisk ratingKinopoisk;

    @OneToOne
    @JoinColumn(name = "rating_imbd_id")
    private RatingImdb ratingImdb;

    @Lob
    private String description;

    private Integer positiveReview;

    private Integer negativeReview;

}


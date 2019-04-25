package ru.itis.darZam.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rating_kinopoisk")
public class RatingKinopoisk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Float estimation;

    private Integer oneCount;
    private Integer twoCount;
    private Integer threeCount;
    private Integer fourCount;
    private Integer fiveCount;
    private Integer sixCount;
    private Integer sevenCount;
    private Integer eightCount;
    private Integer nineCount;

    @OneToOne
    private Film film;

}

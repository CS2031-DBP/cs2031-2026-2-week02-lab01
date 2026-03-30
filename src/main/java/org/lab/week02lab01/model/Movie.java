package org.lab.week02lab01.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Date releaseDate;

    @Column(nullable = true)
    private Long externalId;

    private String directorName;

    // Constructors
    public Movie() {
    }

    public Movie(String title, Date releaseDate) {
        this.title = title;
        this.releaseDate = releaseDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public Long getExternalId() {
         return this.externalId;
    }

    public String getDirectorName() { return directorName; }

    public void setDirectorName(String directorName) { this.directorName = directorName; }
}

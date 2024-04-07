package com.epf.rentmanager.model;

public class Vehicle {
    private int id;
    private String constructeur;
    private String modele;
    private int nb_places;

    public Vehicle(int id, String constructeur, String modele, int nb_places) {
        this.id = id;
        this.constructeur = constructeur;
        this.modele = modele;
        this.nb_places = nb_places;
    }

    public Vehicle() {

    }

    public int getId() {
        return id;
    }

    public String getConstructeur() {
        return constructeur;
    }

    public String getModele() {
        return modele;
    }

    public int getNb_places() {
        return nb_places;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setConstructeur(String constructeur) {
        this.constructeur = constructeur;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public void setNb_places(int nb_places) {
        this.nb_places = nb_places;
    }

    @Override
    public String toString() {
        return String.format("Le véhicule %d - %s %s a %d disponibles",id,constructeur,modele,nb_places);
    }


}

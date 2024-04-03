package com.epf.rentmanager.ui.cli;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.service.ReservationService;
import com.epf.rentmanager.utils.IOUtils;

import java.time.LocalDate;
import java.util.List;

public class CliReservation {
    private final ReservationService reservationService;
    private final CliClient cliClient;
    private final CliVehicule cliVehicule;

    public CliReservation(ReservationService reservationService,CliClient cliClient,CliVehicule cliVehicule) {
        this.reservationService = reservationService;
        this.cliClient = cliClient;
        this.cliVehicule = cliVehicule;
    }

    public void options() {

        IOUtils.print("""
						  [1] Lister toutes les réservations
						  [2] Lister toutes les réservations associées à un Client donné
						  [3] Lister toutes les réservations associées à un Véhicule donné
						  """);
        int choice = IOUtils.readInt("\nVotre choix : ");
        switch (choice) {
            case 1 -> readAll();
            case 2 -> readForClient();
            case 3 -> readForVehicle();
            default -> IOUtils.print("Option invalide.");
        }
    }

    public void readAll() {
        try {
            for (Reservation reservation : reservationService.findAll()) {
                IOUtils.print(reservation.toString());
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void readForClient() {
        try {
            for (Reservation rent : reservationService.findResaByClientId(cliClient.select().getId())) {
                IOUtils.print(rent.toString());
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void readForVehicle() {
        try {
            for (Reservation reservation : reservationService.findResaByVehicleId(cliVehicule.select().getId())) {
                IOUtils.print(reservation.toString());
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void create() {
        IOUtils.print("Création d'une réservation");
        Reservation reservation = new Reservation();
        try {
            reservation.setClient(cliClient.select());
            reservation.setVehicle(cliVehicule.select());
            reservation.setDebut(IOUtils.readDate("Entrez une date de début de réservation : ", true));
            LocalDate fin;
            do {
                fin = IOUtils.readDate("Entrez une date de fin de réservation : ", true);
            } while (fin.isBefore(reservation.getDebut()));
            reservation.setFin(fin);
            IOUtils.print(String.format("La réservation a été créée avec l'identifiant %d",reservationService.Create(reservation)));
        } catch (ServiceException e) {
            e.printStackTrace();
            IOUtils.print("La réservation n'a pas pu être créée.");
        }
    }

    public Reservation select() throws ServiceException {
        List<Reservation> reservationList = reservationService.findAll();
        int index;
        do {
            for (int i = 0; i < reservationList.size(); i++) {
                IOUtils.print(String.format(" [%d] %s", i+1, reservationList.get(i)));
            }
            index = IOUtils.readInt("Saisissez l'index : ");
        } while (index < 1 || index > reservationList.size());

        return reservationList.get(index - 1);
    }

    public void delete() {
        IOUtils.print("Supprimer une réservation");
        try {
            reservationService.delete(reservationService.findById(select().getId()));
            IOUtils.print("Reservation supprimée.");
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}

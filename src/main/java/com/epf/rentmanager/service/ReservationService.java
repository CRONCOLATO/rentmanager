package com.epf.rentmanager.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import com.epf.rentmanager.dao.ReservationDao;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.dao.ClientDao;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.dao.VehicleDao;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;

import com.epf.rentmanager.model.Reservation;
import org.apache.taglibs.standard.tag.el.core.IfTag;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    private Reservation reservation;
    private ReservationDao reservationDao;

    private static final int JourConsecutifMax = 10;

    private static final int NbJourMax = 31;

    private ReservationService(ReservationDao reservationDao){this.reservationDao = reservationDao;}

    private void validerReservationInfo(Reservation reservation) throws ServiceException {
        List<Reservation> reservations = new ArrayList<>();
        try {
            reservations = reservationDao.findResaByVehicleId(reservation.getVehicle().getId());
            reservations.removeIf(r -> r.getId() == reservation.getId()); // Supprime la réservation actuelle de la liste
        } catch (DaoException e) {
            throw new ServiceException("Erreur lors de la récupération des réservations", e);
        }

        LocalDate debut = reservation.getDebut();
        LocalDate fin = reservation.getFin();


        if (reservation.getFin().isBefore(reservation.getDebut())){
            throw new ServiceException("La date de fin est avant la date de début");
        }
        if (reservation.getVehicle() == null){
            throw new ServiceException("Pas de vehicule entré");
        }
        if (reservation.getClient() == null){
            throw new ServiceException("Pas de client entré");
        }
        if( isVehicleDoubleBooked(reservations, reservation.getDebut(), reservation.getFin())){
            throw new ServiceException("Voiture deja réservé");
        }
        if ( isVehicleReservedConsecutively(reservations, reservation.getDebut(), reservation.getFin())){
            throw new ServiceException("Voiture reservé plus de 10 jours");
        }
        if (isVehicleReservedWithoutBreak(reservations, reservation.getDebut(), reservation.getFin())){
            throw new ServiceException("voiture réservé pour 1 mois");
        }
    }


    private boolean doPeriodsOverlap(LocalDate debut, LocalDate fin) {
        return !fin.isBefore(debut) ;
    }
    private boolean isVehicleDoubleBooked(List<Reservation> reservations, LocalDate debut, LocalDate fin) {
        return reservations.stream().anyMatch(rent -> doPeriodsOverlap(debut, fin));
    }
    private boolean isVehicleReservedConsecutively(List<Reservation> reservations, LocalDate debut, LocalDate fin) {
        long consecutiveDays = debut.datesUntil(fin.plusDays(1))
                .filter(date -> reservations.stream().anyMatch(reservation -> reservation.getDebut().compareTo(date) <= 0 && reservation.getFin().compareTo(date) >= 0))
                .count();
        return consecutiveDays > JourConsecutifMax;
    }
    private boolean isVehicleReservedWithoutBreak(List<Reservation> reservations, LocalDate debut, LocalDate fin) {
        long reservedDays = debut.datesUntil(fin.plusDays(1))
                .filter(date -> reservations.stream().anyMatch(reservation -> reservation.getDebut().compareTo(date) <= 0 && reservation.getFin().compareTo(date) >= 0))
                .count();
        return reservedDays > NbJourMax;
    }

    public long Create(Reservation reservation) throws ServiceException {
        validerReservationInfo(reservation);
        try {
            return this.reservationDao.create(reservation);
        }
        catch (DaoException e){
            e.printStackTrace();
            throw new ServiceException();
        }
    }

    public List<Reservation> findAll() throws ServiceException {
        try {
            List<Reservation> reservations = reservationDao.findAll();
            if (reservations.isEmpty()) {
                System.out.println("Aucune réservation trouvée dans la base de données.");
            } else {
                System.out.println("Nombre total de réservations trouvées : " + reservations.size());
                for (Reservation reservation : reservations) {
                    System.out.println("ID : " + reservation.getId() + "client id :"+reservation.getClient().getId()+"vehicle id : "+reservation.getVehicle().getId() + ", Début : " + reservation.getDebut() + ", Fin : " + reservation.getFin());
                }
            }
            return reservations;
        } catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException("Erreur lors de la récupération des réservations", e);
        }
    }


    public void update(Reservation reservation) throws ServiceException {
        validerReservationInfo(reservation);
        try {
            this.reservationDao.update(reservation);
        } catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException();
        }
    }

    public void delete(Reservation reservation) throws ServiceException {
        try {
            this.reservationDao.delete(reservation);
        } catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException();
        }
    }

    public List<Reservation> findResaByClientId(int id) throws ServiceException {
        try {
            return reservationDao.findResaByClientId(id);
        } catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException();
        }
    }


    public List<Reservation> findResaByVehicleId(int id) throws ServiceException {
        try {
            return reservationDao.findResaByVehicleId(id);
        } catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException();
        }
    }


    public Reservation findById(int id) throws ServiceException {
        try {
            return reservationDao.findById(id);
        } catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException(e);
        }
    }

    public int getCount() throws ServiceException {
        try{
            return reservationDao.getCount();
        }
        catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException();
        }
    }

    public List<Vehicle> findVehiclesResaByClient(int client_id) throws ServiceException {
        try{
            return reservationDao.findVehiclesResaByClient(client_id);
        }
        catch (DaoException e) {
            e.printStackTrace();
            throw new ServiceException();
        }
    }

}

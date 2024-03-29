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


public class ReservationService {

    private Reservation reservation;
    private ReservationDao reservationDao;

    private static final int JourConsecutifMax = 10;

    private static final int NbJourMax = 31;

    private ReservationService(ReservationDao reservationDao){this.reservationDao = reservationDao;}

    private void validerReservationInfo( Reservation reservation) throws ServiceException{
        List <Reservation> reservations;
        try {
            reservations = reservationDao.findResaByVehicleId(reservation.getVehicle().getId());
            reservations.remove(reservationDao.findByID(reservation.getId()));
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
        LocalDate Debut = reservation.getDebut();
        LocalDate Fin = reservation.getFin();

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
        return reservations.stream().anyMatch(rent -> doPeriodsOverlap(reservation.getDebut(), reservation.getFin()));
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




}

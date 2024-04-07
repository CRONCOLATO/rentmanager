package com.epf.rentmanager.ui.cli;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.service.ReservationService;
import com.epf.rentmanager.service.VehicleService;
import com.epf.rentmanager.utils.IOUtils;

import java.time.LocalDate;
import java.util.List;

public class CliVehicule {

    private final VehicleService vehicleService;
    private ReservationService reservationService;

    public CliVehicule(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public void readAll() {
        try {
            for (Vehicle vehicle : vehicleService.findAll()) {
                IOUtils.print(vehicle.toString());
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public  void create() {
        IOUtils.print("Création d'un véhicule");
        String constructor = IOUtils.readString("Entrez le nom du contructeur: ", true);
        String model = IOUtils.readString("Entrez le modèle: ", false);
        int seatCount;
        do {
            seatCount = IOUtils.readInt("Entrez le nombre de sièges (1–200): ");
        } while (seatCount < 1 || seatCount > 200);
        Vehicle veh = new Vehicle(0, constructor, model, (short) seatCount);
        try {
            IOUtils.print(String.format("Véhicule créé avec l'identifiant %d",vehicleService.create(veh)));
        } catch (ServiceException e) {
            e.printStackTrace();
            IOUtils.print("Le véhicule n'a pas pu être créé.");
        }
    }

    public  Vehicle select() throws ServiceException {
        IOUtils.print("Sélectionner un véhicule");
        List<Vehicle> vehicleList = vehicleService.findAll();
        int index;
        do {
            for (int i = 0; i < vehicleList.size(); i++) {
                IOUtils.print(String.format(" [%d] %s", i+1, vehicleList.get(i)));
            }
            index = IOUtils.readInt("Saisissez l'index : ");
        } while (index < 1 || index > vehicleList.size());
        return vehicleList.get(index - 1);
    }

    public void delete() {
        IOUtils.print("Supprimer un véhicule");
        try {
            Vehicle selectedVehicle = vehicleService.findById(select().getId());

            LocalDate start = IOUtils.readDate("Entrez la date de début (AAAA-MM-JJ) : ");
            LocalDate end = IOUtils.readDate("Entrez la date de fin (AAAA-MM-JJ) : ");

            List<Reservation> reservations = reservationService.findResaByVehicleId(selectedVehicle.getId(), start, end);
            if (!reservations.isEmpty()) {
                IOUtils.print("Impossible de supprimer ce véhicule : des réservations y sont associées.");
                return;
            }

            vehicleService.delete(selectedVehicle, start, end);

            IOUtils.print("Vehicle supprimé.");
        } catch (ServiceException e) {
            e.printStackTrace();
            IOUtils.print("Une erreur est survenue lors de la suppression du véhicule.");
        }
    }



}

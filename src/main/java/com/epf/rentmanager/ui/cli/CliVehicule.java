package com.epf.rentmanager.ui.cli;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.service.VehicleService;
import com.epf.rentmanager.utils.IOUtils;

import java.util.List;

public class CliVehicule {

    private final VehicleService vehicleService;

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

    public  void delete() {
        IOUtils.print("Supprimer un véhicule");
        try {
            vehicleService.delete(vehicleService.findById(select().getId()));
            IOUtils.print("Vehicle supprimé.");
        } catch (ServiceException e) {
            e.printStackTrace();
            IOUtils.print("Impossible de supprimer ce véhicule : des réservations y sont associées.");
        }
    }

}

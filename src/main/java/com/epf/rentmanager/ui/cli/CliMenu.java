package com.epf.rentmanager.ui.cli;

import com.epf.rentmanager.configuration.AppConfiguration;
import com.epf.rentmanager.service.ClientService;
import com.epf.rentmanager.service.ReservationService;
import com.epf.rentmanager.service.VehicleService;
import com.epf.rentmanager.utils.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CliMenu {
    private static boolean exit = false;
    private final CliClient cliClient;
    private final CliVehicule cliVehicule;
    private final CliReservation cliRent;

    public CliMenu() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
        ClientService clientService = context.getBean(ClientService.class);
        VehicleService vehicleService = context.getBean(VehicleService.class);
        ReservationService reservationService = context.getBean(ReservationService.class);

        cliClient = new CliClient(clientService);
        cliVehicule = new CliVehicule(vehicleService);
        cliRent = new CliReservation(reservationService, cliClient, cliVehicule);
    }

    public void start() {
        IOUtils.print("CLI - RENT MANAGER : Bienvenue\n");
        while (!exit) {
            showMenu();
        }
    }

    public void showMenu() {

        IOUtils.print("""
        --------------------------------

        Action souhaitée :

        [1] Lister des éléments
        [2] Créer des éléments
        [3] Supprimer des éléments
        [4] Quitter CLI
						  """);
        int choice = IOUtils.readInt("\nVotre choix : ");
        switch (choice) {
            case 1 -> displayListOptions();
            case 2 -> displayCreateOptions();
            case 3 -> displayDeleteOptions();
            case 4 -> {
                IOUtils.print("A bientôt !");
                exit = true;
            }
            default -> IOUtils.print("Option invalide.");
        }
    }

    public void displayListOptions() {

        IOUtils.print("""
						  [1] Lister les clients
						  [2] Lister les véhicules
						  [3] Lister les réservations
						  [4] Quitter CLI\
						  """);

        int choice = IOUtils.readInt("\nVotre choix : ");
        switch (choice) {
            case 1 -> cliClient.readAll();
            case 2 -> cliVehicule.readAll();
            case 3 -> cliRent.options();
            case 4 -> {
                IOUtils.print("A bientôt !");
                exit = true;
            }
            default -> IOUtils.print("Option invalide.");
        }
    }

    public void displayCreateOptions() {

        IOUtils.print("""
						  [1] Créer un client
						  [2] Créer un véhicule
						  [3] Créer une réservation
						  [4] Quitter CLI\
						  """);

        int choice = IOUtils.readInt("\nVotre choix : ");
        switch (choice) {
            case 1 -> cliClient.create();
            case 2 -> cliVehicule.create();
            case 3 -> cliRent.create();
            case 4 -> {
                IOUtils.print("A bientôt !");
                exit = true;
            }
            default -> IOUtils.print("Option invalide.");
        }
    }

    public void displayDeleteOptions() {

        IOUtils.print("""
						  [1] Supprimer un client
						  [2] Supprimer un véhicule
						  [3] Supprimer une réservation
						  [4] Quitter CLI\
						  """);

        int choice = IOUtils.readInt("\nVotre choix : ");
        switch (choice) {
            case 1 -> cliClient.delete();
            case 2 -> cliVehicule.delete();
            case 3 -> cliRent.delete();
            case 4 -> {
                IOUtils.print("A bientôt !");
                exit = true;
            }
            default -> IOUtils.print("Option invalide.");
        }
    }
}

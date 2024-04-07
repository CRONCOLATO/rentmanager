package com.epf.rentmanager.ui.cli;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.service.ClientService;
import com.epf.rentmanager.utils.IOUtils;

import java.time.LocalDate;
import java.util.List;

public class CliClient {
    private final ClientService clientService;

    public CliClient(ClientService clientService) {
        this.clientService = clientService;
    }

    public void readAll() {
        try {
            for (Client client : clientService.findAll()) {
                IOUtils.print(client.toString());
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    public void create() {
        IOUtils.print("Création d'un client");
        String firstName = IOUtils.readString("Entrez le prénom", true);
        String lastName = IOUtils.readString("Entrez le nom: ", true).toUpperCase();
        LocalDate birthDate = IOUtils.readDate("Entre la date de naissance (jj/mm/aaaa): ");
        String email;
        do {
            email = IOUtils.readString("Entrez l'adresse courriel: ", true);
        } while (!email.matches("^(.+)@(\\S+)$"));
        Client cli = new Client(0, lastName, firstName, email, birthDate);
        try {
            IOUtils.print(String.format("Le client a été créé avec l'identifiant %d", clientService.create(cli)));
        } catch (ServiceException e) {
            e.printStackTrace();
            IOUtils.print("Le client n'a pas pu être créé.");
        }
    }

    public Client select() throws ServiceException {
        IOUtils.print("Sélectionner un client");
        List<Client> clientList = clientService.findAll();
        int index;
        do {
            for (int i = 0; i < clientList.size(); i++) {
                IOUtils.print(String.format(" [%d] %s", i+1, clientList.get(i)));
            }
            index = IOUtils.readInt("Saisissez l'index : ");
        } while (index < 1 || index > clientList.size());

        return clientList.get(index - 1);
    }

    public void delete() {
        IOUtils.print("Supprimer un client");
        try {
            clientService.delete(clientService.findById(select().getId()));
            IOUtils.print("Client supprimé.");
        } catch (ServiceException e){
            e.printStackTrace();
            IOUtils.print("Impossible de supprimer ce client : des réservations y sont associées.");
        }
    }
}

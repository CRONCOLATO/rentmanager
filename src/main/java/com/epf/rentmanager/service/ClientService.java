package com.epf.rentmanager.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import com.epf.rentmanager.dao.ClientDao;
import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.dao.ReservationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

	private static final int AgeMinimum = 18;
	private ClientDao clientDao;
	@Autowired
	private ReservationDao reservationDao;

	public static ClientService instance;
	
	private ClientService() {
		this.clientDao = ClientDao.getInstance();
	}
	
	public static ClientService getInstance() {
		if (instance == null) {
			instance = new ClientService();
		}
		
		return instance;
	}

	private Client validerClientInfo(Client client, int email_count) throws ServiceException, DaoException{
		String nom = client.getNom();
		String prenom = client.getPrenom();
		int age = Period.between(client.getNaissance(), LocalDate.now()).getYears();
        String email = client.getEmail();
		if (nom.isEmpty() || prenom.isEmpty()){
			throw new ServiceException("Nom ou prénom non rentré");
		}
		if (nom.length() < 2 || prenom.length() < 2){
			throw new ServiceException("le nom et prenom doivent comporter 2 caractère");
		}
		if (age < AgeMinimum) {
			throw new ServiceException(String.format("Le client doit avoir %d ans", AgeMinimum));
		}
		client.setNom(nom.toUpperCase());
		return client;
    }
	
	public long create(Client client) throws ServiceException {
		try {
			client = validerClientInfo(client, 0);
			return this.clientDao.create(client);
		} catch (DaoException e) {
			e.printStackTrace();
            throw new ServiceException();
        }
	}

	public Client findById(long id) throws ServiceException {
		try {
			return this.clientDao.findById(id);
		}
		catch (DaoException e) {
			e.printStackTrace();
			throw new ServiceException();
		}
	}

	public List<Client> findAll() throws ServiceException {
		try {
			return this.clientDao.findAll();
		}
		catch (DaoException e){
			e.printStackTrace();
			throw new ServiceException();
		}
	}

	public void delete(Client client) throws ServiceException {
		try {
			for(Reservation reservation : reservationDao.findResaByClientId(client.getId())){
				reservationDao.delete(reservation);
			}
			clientDao.delete(client);
		} catch (DaoException e) {
			e.printStackTrace();
			throw new ServiceException();
		}
	}

	public int getCount() throws ServiceException {
		try{
			return this.clientDao.getCount();
		}
		catch (DaoException e) {
			e.printStackTrace();
			throw new ServiceException();
		}
	}

	public void update(Client client) throws ServiceException {
		try {
			client = validerClientInfo(client,1);
			this.clientDao.update(client);
		} catch (DaoException e) {
			e.printStackTrace();
			throw new ServiceException();
		}
	}

}

package com.epf.rentmanager.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.persistence.ConnectionManager;

public class ClientDao {
	
	private static ClientDao instance = null;
	private ClientDao() {}
	public static ClientDao getInstance() {
		if(instance == null) {
			instance = new ClientDao();
		}
		return instance;
	}
	
	private static final String CREATE_CLIENT_QUERY = "INSERT INTO Client(nom, prenom, email, naissance) VALUES(?, ?, ?, ?);";
	private static final String DELETE_CLIENT_QUERY = "DELETE FROM Client WHERE id=?;";
	private static final String FIND_CLIENT_QUERY = "SELECT nom, prenom, email, naissance FROM Client WHERE id=?;";
	private static final String FIND_CLIENTS_QUERY = "SELECT id, nom, prenom, email, naissance FROM Client;";
	private static final String UPDATE_CLIENT_QUERY = "UPDATE Client SET nom=?, prenom=?, email=?, naissance=? WHERE id=?";


	public long create(Client client) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(CREATE_CLIENT_QUERY, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, client.getNom());
			statement.setString(2, client.getPrenom());
			statement.setString(3, client.getEmail());
			statement.setDate(4, Date.valueOf(client.getNaissance()));
			int id = statement.executeUpdate();
			return id;
		}
		catch (SQLException e){
			throw new DaoException("Erreur création client");
		}
	}
	
	public long delete(Client client) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(DELETE_CLIENT_QUERY, Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, client.getId());
			int id = statement.executeUpdate();
			return id;

		}
		catch (SQLException e){
			throw new DaoException("Erreur dans la suppréssion du client");
		}
	}

	public Client findById(long id) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(FIND_CLIENT_QUERY);
			statement.setLong(1, id);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			String nom = resultSet.getString("nom");
			String prenom = resultSet.getString("prenom");
			String email = resultSet.getString("email");
			LocalDate naissance = resultSet.getDate("naissance").toLocalDate();
			return new Client((int) id, nom, prenom, email, naissance);
		}
		catch (SQLException e){
			throw new DaoException("ID incorrect");
		}
	}

	public List<Client> findAll() throws DaoException {
		List<Client> clients = new ArrayList<Client>();
		try {
			Connection connection = ConnectionManager.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet =statement.executeQuery(FIND_CLIENTS_QUERY);
			while (resultSet.next()){
				int id =resultSet.getInt("id");
				String nom = resultSet.getString("nom");
				String prenom = resultSet.getString("prenom");
				String email = resultSet.getString("email");
				LocalDate naissance = resultSet.getDate("naissance").toLocalDate();
				clients.add(new Client(id, nom, prenom, email, naissance));
			}
		}
		catch (SQLException e){
			throw new DaoException("Erreur lors de la récupération de la liste");
		}
		return clients;
	}

	public void update(Client client) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(UPDATE_CLIENT_QUERY);
			statement.setString(1, client.getPrenom());
			statement.setString(2, client.getNom());
			statement.setString(3, client.getEmail());
			statement.setDate(4, Date.valueOf(client.getNaissance()));
			statement.setLong(5, client.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException("Erreur lors de l'update");
		}
	}
}

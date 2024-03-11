package com.epf.rentmanager.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.persistence.ConnectionManager;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.model.Client;

public class ReservationDao {

	private static ReservationDao instance = null;
	private ReservationDao() {}
	public static ReservationDao getInstance() {
		if(instance == null) {
			instance = new ReservationDao();
		}
		return instance;
	}
	
	private static final String CREATE_RESERVATION_QUERY = "INSERT INTO Reservation(client_id, vehicle_id, debut, fin) VALUES(?, ?, ?, ?);";
	private static final String DELETE_RESERVATION_QUERY = "DELETE FROM Reservation WHERE id=?;";
	private static final String FIND_RESERVATIONS_BY_CLIENT_QUERY = "SELECT id, vehicle_id, debut, fin FROM Reservation WHERE client_id=?;";
	private static final String FIND_RESERVATIONS_BY_VEHICLE_QUERY = "SELECT id, client_id, debut, fin FROM Reservation WHERE vehicle_id=?;";
	private static final String FIND_RESERVATIONS_QUERY = "SELECT id, client_id, vehicle_id, debut, fin FROM Reservation;";
		
	public long create(Reservation reservation) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(CREATE_RESERVATION_QUERY, Statement.RETURN_GENERATED_KEYS);
			statement.setLong(1, reservation.getClient().getId());
			statement.setLong(2, reservation.getVehicle().getId());
			statement.setDate(3, Date.valueOf(reservation.getDebut()));
			statement.setDate(4, Date.valueOf(reservation.getFin()));
			int id = statement.executeUpdate();
			return id;
		}
		catch (SQLException e){
			throw new DaoException("Erreur de création de la reservation");
		}
	}
	
	public long delete(Reservation reservation) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(DELETE_RESERVATION_QUERY, Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, reservation.getId());
			int id = statement.executeUpdate();
			return id;

		}
		catch (SQLException e){
			throw new DaoException("Erreur dans la suppréssion de a reservation");
		}
	}

	
	public List<Reservation> findResaByClientId(long clientId) throws DaoException {
		List<Reservation> reservations = new ArrayList<Reservation>();
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(FIND_RESERVATIONS_BY_CLIENT_QUERY);
			statement.setObject(1, clientId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()){
				int id = resultSet.getInt("id");
				long vehicle = resultSet.getInt("vehicle_id");
				LocalDate debut = resultSet.getDate("debut").toLocalDate();
				LocalDate fin = resultSet.getDate("fin").toLocalDate();
				reservations.add(new Reservation( id, clientId, vehicle, debut, fin));
			}
		}
		catch (SQLException e){
			throw new DaoException("ID client incorrect");
		}
		return reservations;
	}

	public List<Reservation> findResaByVehicleId(Long vehicleId) throws DaoException {
		List<Reservation> reservations = new ArrayList<Reservation>();
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(FIND_RESERVATIONS_BY_VEHICLE_QUERY);
			statement.setObject(1, vehicleId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()){
				int id = resultSet.getInt("id");
				Client client = (Client) resultSet.getObject("client");
				LocalDate debut = resultSet.getDate("debut").toLocalDate();
				LocalDate fin = resultSet.getDate("fin").toLocalDate();
				reservations.add(new Reservation((int)id, client.getId(), vehicleId, debut, fin));
			}
		}
		catch (SQLException e){
			throw new DaoException("ID Vehicule incorrect");
		}
		return reservations;
	}

	public Reservation findByID(int id) throws DaoException {
		Reservation reservation = null;
		try (
				Connection connection = ConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(FIND_RESERVATIONS_QUERY)
		) {
			preparedStatement.setLong(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Client client = new Client(resultSet.getInt("clientId"), resultSet.getString("nom"),resultSet.getString("prenom"), resultSet.getString("email"),resultSet.getDate("naissance").toLocalDate());
				Vehicle vehicle = new Vehicle(resultSet.getInt("vehicleId"),resultSet.getString("constructeur"), resultSet.getString("modele"),resultSet.getShort("nb_places"));
				reservation = new Reservation(id, client.getId(), vehicle.getId(), resultSet.getDate("debut").toLocalDate(),resultSet.getDate("fin").toLocalDate());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
		return reservation;
	}


	public List<Reservation> findAll() throws DaoException {
		//todo
		List<Reservation> reservations = new ArrayList<Reservation>();
		return reservations;
	}

}

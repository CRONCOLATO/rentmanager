package com.epf.rentmanager.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.persistence.ConnectionManager;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.service.ClientService;
import com.epf.rentmanager.service.VehicleService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.epf.rentmanager.configuration.AppConfiguration;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationDao {

	private static ReservationDao instance = null;
	private ReservationDao() {}
	public static ReservationDao getInstance() {
		if(instance == null) {
			instance = new ReservationDao();
		}
		return instance;
	}

	private static final String CLIENT_FIELDS = "Client.id, nom, prenom, email, naissance";
	private static final String VEHICLE_FIELDS = "Vehicle.id, constructeur, modele, nb_places";
	private static final String INNER_JOIN_TABLES = "INNER JOIN Client ON Reservation.client_id = Client.id INNER JOIN Vehicle ON Reservation.vehicle_id = Vehicle.id";
	private static final String CREATE_RESERVATION_QUERY = "INSERT INTO Reservation(client_id, vehicle_id, debut, fin) VALUES(?, ?, ?, ?);";
	private static final String DELETE_RESERVATION_QUERY = "DELETE FROM Reservation WHERE id=?;";
	private static final String FIND_RESERVATIONS_BY_CLIENT_QUERY = "SELECT id, vehicle_id, debut, fin FROM Reservation WHERE client_id=?;";
	private static final String FIND_RESERVATIONS_BY_VEHICLE_QUERY = "SELECT id, client_id, debut, fin FROM Reservation WHERE vehicle_id=?;";
	private static final String FIND_RESERVATIONS_QUERY = "SELECT id, client_id, vehicle_id, debut, fin FROM Reservation;";
	private static final String FIND_RESERVATION_QUERY = String.format("SELECT Reservation.id, Reservation.client_id, vehicle_id, debut, fin, %s, %s FROM Reservation %s WHERE Reservation.id=?;",CLIENT_FIELDS,VEHICLE_FIELDS,INNER_JOIN_TABLES);
	private static final String UPDATE_RENT_QUERY = "UPDATE Reservation SET client_id=?, vehicle_id=?, debut=?, fin=? WHERE id=?;";
	private static final String COUNT_RESERVATIONS_QUERY = "SELECT COUNT(id) AS count FROM Reservation;";
	private static final String FIND_VEHICLES_RESA_BY_CLIENT_QUERY = String.format("SELECT %s FROM Reservation INNER JOIN Vehicle ON Reservation.vehicle_id = Vehicle.id WHERE Reservation.client_id=?;", VEHICLE_FIELDS);



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


	public List<Reservation> findResaByClientId(int clientId) throws DaoException {
		List<Reservation> reservations = new ArrayList<Reservation>();
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(FIND_RESERVATIONS_BY_CLIENT_QUERY);
			statement.setObject(1, clientId);
			ResultSet resultSet = statement.executeQuery();

			Object[] services = getServices();
			ClientService clientService = (ClientService) services[0];
			VehicleService vehicleService = (VehicleService) services[1];

			while (resultSet.next()){
				int id = resultSet.getInt("id");
				Client client = clientService.findById(resultSet.getInt("client_id"));
				Vehicle vehicle = vehicleService.findById(resultSet.getInt("vehicle_id"));
				LocalDate debut = resultSet.getDate("debut").toLocalDate();
				LocalDate fin = resultSet.getDate("fin").toLocalDate();
				reservations.add(new Reservation( id, client, vehicle, debut, fin));
			}
		}
		catch (SQLException e){
			throw new DaoException("ID client incorrect");
		} catch (ServiceException e) {
			throw new RuntimeException(e);
		}
		return reservations;
	}

	public List<Reservation> findResaByVehicleId(int vehicle_Id) throws DaoException {
		List<Reservation> reservations = new ArrayList<Reservation>();
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(FIND_RESERVATIONS_BY_VEHICLE_QUERY);
			statement.setObject(1, vehicle_Id);
			ResultSet resultSet = statement.executeQuery();

			Object[] services = getServices();
			ClientService clientService = (ClientService) services[0];
			VehicleService vehicleService = (VehicleService) services[1];

			while (resultSet.next()){
				int id = resultSet.getInt("id");
				LocalDate debut = resultSet.getDate("debut").toLocalDate();
				LocalDate fin = resultSet.getDate("fin").toLocalDate();
				Client client = clientService.findById(resultSet.getInt("client_id"));
				Vehicle vehicle = vehicleService.findById(resultSet.getInt("vehicle_id"));

				System.out.println("Réservation trouvée - ID: " + id +", client id : " + client + ", vehicle id : "+ vehicle + ", Début: " + debut + ", Fin: " + fin);

				reservations.add(new Reservation(id, client, vehicle, debut, fin));
			}
		}
		catch (SQLException e){
			System.err.println("Erreur SQL lors de la récupération des réservations pour le véhicule avec l'ID: " + vehicle_Id);
			e.printStackTrace();
			throw new DaoException("ID Vehicule incorrect");
		} catch (ServiceException e) {
			System.err.println("Erreur de service lors de la récupération des réservations pour le véhicule avec l'ID: " + vehicle_Id);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return reservations;
	}



	public Reservation findByID(int id) throws DaoException {
		Reservation reservation = null;
		try (
				Connection connection = ConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(FIND_RESERVATION_QUERY)
		) {
			preparedStatement.setLong(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Client client = new Client(resultSet.getInt("clientId"), resultSet.getString("nom"),resultSet.getString("prenom"), resultSet.getString("email"),resultSet.getDate("naissance").toLocalDate());
				Vehicle vehicle = new Vehicle(resultSet.getInt("vehicleId"),resultSet.getString("constructeur"), resultSet.getString("modele"),resultSet.getShort("nb_places"));
				reservation = new Reservation(id, client , vehicle, resultSet.getDate("debut").toLocalDate(),resultSet.getDate("fin").toLocalDate());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
		return reservation;
	}

	public List<Reservation> findAll() throws DaoException {
		List<Reservation> reservations = new ArrayList<Reservation>();
		try{
			Connection connection = ConnectionManager.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(FIND_RESERVATIONS_QUERY);

			Object[] services = getServices();
			ClientService clientService = (ClientService) services[0];
			VehicleService vehicleService = (VehicleService) services[1];

			while (resultSet.next()){
				int id = resultSet.getInt("id");
				Client client = clientService.findById(resultSet.getInt("client_id"));
				Vehicle vehicle = vehicleService.findById(resultSet.getInt("vehicle_id"));
				LocalDate debut = resultSet.getDate("debut").toLocalDate();
				LocalDate fin = resultSet.getDate("fin").toLocalDate();
				reservations.add(new Reservation(id, client, vehicle, debut,fin));
			}
		} catch (SQLException | ServiceException e) {
			throw new DaoException(e.getMessage());
		}
		return reservations;
	}


	private Object[] getServices() {
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
		return new Object[] {
				context.getBean(ClientService.class),
				context.getBean(VehicleService.class),
		};
	}

	public void update(Reservation reservation) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(UPDATE_RENT_QUERY);
			statement.setInt(1, reservation.getClient().getId());
			statement.setLong(2, reservation.getVehicle().getId());
			statement.setDate(3, Date.valueOf(reservation.getDebut()));
			statement.setDate(4, Date.valueOf(reservation.getFin()));
			statement.setInt(5,reservation.getId());
			int rowsUpdated = statement.executeUpdate();
			if (rowsUpdated == 0) {
				throw new DaoException("La mise à jour de la réservation a échoué, aucune ligne mise à jour dans la base de données.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DaoException("Erreur lors de la mise à jour de la réservation dans la base de données.", e);
		}
	}


	public Reservation findById(int id) throws DaoException {
		Reservation reservation = null;
		try (
				Connection connection = ConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(FIND_RESERVATION_QUERY)
		) {
			preparedStatement.setLong(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Client client = new Client(resultSet.getInt("client_id"), resultSet.getString("nom"),resultSet.getString("prenom"), resultSet.getString("email"),resultSet.getDate("naissance").toLocalDate());
				Vehicle vehicle = new Vehicle(resultSet.getInt("vehicle_id"),resultSet.getString("constructeur"), resultSet.getString("modele"),resultSet.getShort("nb_places"));
				reservation = new Reservation(id, client, vehicle, resultSet.getDate("debut").toLocalDate(),resultSet.getDate("fin").toLocalDate());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
		return reservation;
	}


	public int getCount() throws DaoException{
		try{
			Connection connection = ConnectionManager.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(COUNT_RESERVATIONS_QUERY);
			resultSet.next();
			return resultSet.getInt("count");
		}
		catch (SQLException e) {
			throw new DaoException(e.getMessage());
		}
	}

	public List<Vehicle> findVehiclesResaByClient(int client_id) throws DaoException {
		List<Vehicle> clientVehicles = new ArrayList<>();
		try (
				Connection connection = ConnectionManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(FIND_VEHICLES_RESA_BY_CLIENT_QUERY)
		) {
			preparedStatement.setLong(1, client_id);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				clientVehicles.add(new Vehicle(resultSet.getInt("Vehicle.id"), resultSet.getString("constructeur"),resultSet.getString("modele"), resultSet.getInt("nb_places")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
		return clientVehicles;
	}


}

package br.edu.ifba.inf008;

import br.edu.ifba.inf008.interfaces.IDataProvider;
import br.edu.ifba.inf008.model.Customer;
import br.edu.ifba.inf008.model.Rental;
import br.edu.ifba.inf008.model.Vehicle;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class MariaDBProvider implements IDataProvider {

    // Configuração do Banco
    private static final String URL = "jdbc:mariadb://localhost:3306/car_rental_system";
    private static final String USER = "root";
    private static final String PASS = "root";

    // Conectar ao banco
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        // Query para pegar clientes
        String sql = "SELECT * FROM customers";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Criando o objeto Cliente com dados do banco
                Customer c = new Customer();
                c.setCpf(rs.getString("cpf"));
                c.setName(rs.getString("name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                customers.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao buscar clientes: " + e.getMessage());
        }
        return customers;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return getVehiclesByQuery("SELECT * FROM vehicles");
    }

    @Override
    public List<Vehicle> getVehiclesByType(Vehicle.VehicleType type) {
        // Busca veículos de um tipo específico que estejam DISPONÍVEIS
        String sql = "SELECT * FROM vehicles WHERE type = '" + type.name() + "' AND status = 'AVAILABLE'";
        return getVehiclesByQuery(sql);
    }

    // Método auxiliar para não repetir código
    private List<Vehicle> getVehiclesByQuery(String sql) {
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setId(rs.getString("license_plate"));
                v.setMake(rs.getString("make"));
                v.setModel(rs.getString("model"));
                v.setYear(rs.getInt("year"));
                v.setOdometer(rs.getInt("odometer"));

                // Convertendo texto do banco para ENUMs do Java
                v.setType(Vehicle.VehicleType.valueOf(rs.getString("type")));
                v.setStatus(Vehicle.VehicleStatus.valueOf(rs.getString("status")));
                v.setFuelType(Vehicle.FuelType.valueOf(rs.getString("fuel_type")));
                v.setTransmission(Vehicle.Transmission.valueOf(rs.getString("transmission")));

                vehicles.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao buscar veículos: " + e.getMessage());
        }
        return vehicles;
    }

    @Override
    public List<Rental> getActiveRentals() {
        return new ArrayList<>(); // Deixamos vazio por enquanto
    }

    @Override
    public boolean saveRental(Rental rental) {
        String sql = "INSERT INTO rentals (customer_cpf, vehicle_plate, start_date, end_date, total_value, status) VALUES (?, ?, ?, ?, ?, 'ACTIVE')";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rental.getCustomer().getCpf());
            pstmt.setString(2, rental.getVehicle().getId());
            pstmt.setTimestamp(3, Timestamp.valueOf(rental.getStartDate()));
            pstmt.setTimestamp(4, Timestamp.valueOf(rental.getEndDate()));
            pstmt.setBigDecimal(5, rental.getTotalValue());

            int rows = pstmt.executeUpdate();
            if(rows > 0) {
                updateVehicleStatus(rental.getVehicle().getId(), Vehicle.VehicleStatus.RENTED);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateVehicleStatus(String licensePlate, Vehicle.VehicleStatus newStatus) {
        String sql = "UPDATE vehicles SET status = ? WHERE license_plate = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.name());
            pstmt.setString(2, licensePlate);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateRentalReturn(int rentalId, LocalDateTime endDate, BigDecimal totalValue) {
        return false;
    }
}
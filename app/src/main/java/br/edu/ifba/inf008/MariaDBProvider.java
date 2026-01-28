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

    private static final String URL = "jdbc:mariadb://localhost:3306/car_rental_system";
    private static final String USER = "root";
    private static final String PASS = "root";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer c = new Customer();
                c.setCpf(rs.getString("tax_id"));

                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");

                c.setName(fullName);
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

        String sql = "SELECT v.*, vt.type_name " +
                     "FROM vehicles AS v " +
                     "JOIN vehicle_types AS tp ON v.type_id = vt.type_id";

        return getVehiclesByQuery(sql);
    }

    @Override
    public List<Vehicle> getVehiclesByType(Vehicle.VehicleType type) {
        // Busca veículos de um tipo específico que estejam DISPONÍVEIS
        String sql = "SELECT v.*, vt.type_name " +
                     "FROM vehicles AS v " +
                     "JOIN vehicle_types AS vt ON v.type_id = vt.type_id " +
                     "WHERE vt.type_name = '" + type.name() + "' " +
                     "AND v.status = 'AVAILABLE'";
        return getVehiclesByQuery(sql);
    }

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

                v.setOdometer((int) rs.getDouble("mileage"));

                String typeName = rs.getString("type_name");
                try{
                    v.setType(Vehicle.VehicleType.valueOf(typeName));
                } catch(IllegalArgumentException e) {
                    System.err.println("Tipo desconhecido:  " + typeName);
                }

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
        return new ArrayList<>();
    }

    @Override
    public boolean saveRental(Rental rental) {
        String sql = "INSERT INTO rentals (" +
                     "customer_id, vehicle_id, start_date, scheduled_end_date, " +
                     "total_amount, rental_status, rental_type, pickup_location, " +
                     "base_rate, insurance_fee, initial_mileage) " +
                     "VALUES (" +
                     "(SELECT customer_id FROM customers WHERE email = ? LIMIT 1), " +
                     "(SELECT vehicle_id FROM vehicles WHERE license_plate = ? LIMIT 1), " +
                     "?, ?, ?, 'ACTIVE', 'DAILY', 'Loja Principal', 100.00, 50.00, " +
                     "(SELECT mileage FROM vehicles WHERE license_plate = ? LIMIT 1))";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rental.getCustomer().getEmail());

            pstmt.setString(2, rental.getVehicle().getId());

            pstmt.setTimestamp(3, Timestamp.valueOf(rental.getStartDate()));
            pstmt.setTimestamp(4, Timestamp.valueOf(rental.getEndDate()));
            pstmt.setBigDecimal(5, rental.getTotalValue());

            pstmt.setString(6, rental.getVehicle().getId());

            int rows = pstmt.executeUpdate();
            if(rows > 0) {
                updateVehicleStatus(rental.getVehicle().getId(), Vehicle.VehicleStatus.RENTED);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao salvar aluguel: " + e.getMessage());
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
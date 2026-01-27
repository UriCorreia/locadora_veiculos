package br.edu.ifba.inf008; // Notei nos prints que sua pasta é 'shell'

import br.edu.ifba.inf008.shell.Core;
import br.edu.ifba.inf008.MariaDBProvider;
import br.edu.ifba.inf008.interfaces.IDataProvider;
import br.edu.ifba.inf008.interfaces.IVehiclePlugin;
import br.edu.ifba.inf008.model.Customer; // Ajustado para 'model' (singular) conforme seu print
import br.edu.ifba.inf008.model.Rental;
import br.edu.ifba.inf008.model.Vehicle;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class App extends Application { // Nome da classe mantido como App

    private IDataProvider dataProvider;

    @Override
    public void start(Stage primaryStage) {
        // 1. Inicializa Conexão
        // Como MariaDBProvider está na mesma pasta 'shell', não precisa de import
        dataProvider = new MariaDBProvider();

        // 2. Componentes da Tela
        Label lblCliente = new Label("Selecione o Cliente:");
        ComboBox<Customer> cmbCustomers = new ComboBox<>();

        // Tenta buscar clientes. Se o banco falhar, evita fechar o app na cara
        try {
            cmbCustomers.getItems().addAll(dataProvider.getAllCustomers());
        } catch (Exception ex) {
            System.out.println("Erro ao buscar clientes: " + ex.getMessage());
        }

        Label lblTipo = new Label("Tipo de Veículo:");
        ComboBox<Vehicle.VehicleType> cmbType = new ComboBox<>();
        cmbType.getItems().addAll(Vehicle.VehicleType.values());

        Button btnBuscar = new Button("Buscar Veículos Disponíveis");
        ListView<Vehicle> listVehicles = new ListView<>();

        Label lblResultado = new Label();

        // 3. Ação de Buscar
        btnBuscar.setOnAction(e -> {
            Vehicle.VehicleType tipo = cmbType.getValue();
            if (tipo != null) {
                try {
                    List<Vehicle> veiculos = dataProvider.getVehiclesByType(tipo);
                    listVehicles.getItems().setAll(veiculos);
                    if(veiculos.isEmpty()) lblResultado.setText("Nenhum veículo disponível deste tipo.");
                    else lblResultado.setText(veiculos.size() + " veículos encontrados.");
                } catch (Exception ex) {
                    lblResultado.setText("Erro no banco de dados.");
                    ex.printStackTrace();
                }
            }
        });

        Button btnAlugar = new Button("ALUGAR VEÍCULO");

        // 4. Ação de Alugar
        btnAlugar.setOnAction(e -> {
            Customer cliente = cmbCustomers.getValue();
            Vehicle veiculo = listVehicles.getSelectionModel().getSelectedItem();

            if (cliente != null && veiculo != null) {
                // Simulação simples de preço (Plugin entrará aqui depois)
                BigDecimal valorEstimado = new BigDecimal("100.00");

                Rental aluguel = new Rental();
                aluguel.setCustomer(cliente);
                aluguel.setVehicle(veiculo);
                aluguel.setStartDate(LocalDateTime.now());
                aluguel.setEndDate(LocalDateTime.now().plusDays(1));
                aluguel.setTotalValue(valorEstimado);

                if (dataProvider.saveRental(aluguel)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Aluguel realizado com sucesso!");
                    alert.show();
                    btnBuscar.fire(); // Atualiza a lista
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao salvar aluguel.");
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione um Cliente e um Veículo.");
                alert.show();
            }
        });

        // 5. Layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(
                lblCliente, cmbCustomers,
                lblTipo, cmbType,
                btnBuscar,
                new Label("Veículos Disponíveis:"), listVehicles,
                btnAlugar, lblResultado
        );

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setTitle("Locadora de Veículos - INF008");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
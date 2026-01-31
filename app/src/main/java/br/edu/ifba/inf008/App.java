package br.edu.ifba.inf008; // Ajuste o pacote se necessário

import br.edu.ifba.inf008.MariaDBProvider; // Apenas se precisar importar classes concretas
import br.edu.ifba.inf008.interfaces.IDataProvider;
import br.edu.ifba.inf008.model.Customer;
import br.edu.ifba.inf008.model.Rental;
import br.edu.ifba.inf008.model.Vehicle;
import br.edu.ifba.inf008.shell.Core;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Estrutura Base (Microkernel Shell)
        MenuBar menuBar = new MenuBar();
        TabPane rootTabPane = new TabPane();
        VBox mainLayout = new VBox(menuBar, rootTabPane); // Layout Principal

        // 2. Inicializa o Core (Conecta DB, Configura Singleton e UI)
        // O Core recebe o TabPane para que os plugins possam adicionar abas nele
        Core core = new Core(rootTabPane, menuBar);

        // 3. Carrega Plugins (Eles vão aparecer automaticamente no TabPane se tiverem UI)
        System.out.println(" >>> CARREGANDO PLUGINS... ");
        core.getPluginController().init();

        // 4. Cria a Aba de Locação (Seu código de formulário)
        // Isso garante que a funcionalidade principal esteja disponível junto com os plugins
        createRentalTab(rootTabPane);

        // 5. Exibe a Janela
        Scene scene = new Scene(mainLayout, 1024, 768);
        primaryStage.setTitle("Locadora de Veículos - INF008");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Movemos sua lógica para um método auxiliar para não poluir o start()
    private void createRentalTab(TabPane tabPane) {
        // Recupera o DataProvider do Core (Singleton) em vez de criar um novo
        IDataProvider dataProvider = Core.getInstance().getDataProvider();

        Label lblCliente = new Label("Selecione o Cliente:");
        ComboBox<Customer> cmbCustomers = new ComboBox<>();

        // Preenche Clientes
        try {
            cmbCustomers.getItems().addAll(dataProvider.getAllCustomers());
        } catch (Exception ex) {
            System.err.println("Erro ao buscar clientes: " + ex.getMessage());
        }

        Label lblTipo = new Label("Tipo de Veículo:");
        ComboBox<Vehicle.VehicleType> cmbType = new ComboBox<>();
        cmbType.getItems().addAll(Vehicle.VehicleType.values());

        Button btnBuscar = new Button("Buscar Veículos Disponíveis");
        ListView<Vehicle> listVehicles = new ListView<>();
        Label lblResultado = new Label();

        // Lógica de Busca
        btnBuscar.setOnAction(e -> {
            Vehicle.VehicleType tipo = cmbType.getValue();
            if (tipo != null) {
                try {
                    List<Vehicle> veiculos = dataProvider.getVehiclesByType(tipo);
                    listVehicles.getItems().setAll(veiculos);
                    if(veiculos.isEmpty()) lblResultado.setText("Nenhum veículo disponível.");
                    else lblResultado.setText(veiculos.size() + " veículos encontrados.");
                } catch (Exception ex) {
                    lblResultado.setText("Erro no banco.");
                    ex.printStackTrace();
                }
            }
        });

        Button btnAlugar = new Button("ALUGAR VEÍCULO");

        // Lógica de Aluguel
        btnAlugar.setOnAction(e -> {
            Customer cliente = cmbCustomers.getValue();
            Vehicle veiculo = listVehicles.getSelectionModel().getSelectedItem();

            if (cliente != null && veiculo != null) {
                // TODO: Aqui futuramente usaremos o PLUGIN de preço para calcular o valor real
                BigDecimal valorEstimado = new BigDecimal("100.00");

                Rental aluguel = new Rental();
                aluguel.setCustomer(cliente);
                aluguel.setVehicle(veiculo);
                aluguel.setStartDate(LocalDateTime.now());
                aluguel.setEndDate(LocalDateTime.now().plusDays(1));
                aluguel.setTotalValue(valorEstimado);

                if (dataProvider.saveRental(aluguel)) {
                    new Alert(Alert.AlertType.INFORMATION, "Aluguel realizado com sucesso!").show();
                    btnBuscar.fire(); // Atualiza a lista
                } else {
                    new Alert(Alert.AlertType.ERROR, "Erro ao salvar aluguel.").show();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Selecione Cliente e Veículo.").show();
            }
        });

        // Monta o Layout da Aba
        VBox rentalLayout = new VBox(10);
        rentalLayout.setPadding(new Insets(15));
        rentalLayout.getChildren().addAll(
                lblCliente, cmbCustomers,
                lblTipo, cmbType,
                btnBuscar,
                new Label("Veículos Disponíveis:"), listVehicles,
                btnAlugar, lblResultado
        );

        // Adiciona como uma Aba no TabPane principal
        Tab rentalTab = new Tab("Nova Locação");
        rentalTab.setContent(rentalLayout);
        rentalTab.setClosable(false); // Aba fixa
        tabPane.getTabs().add(rentalTab);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
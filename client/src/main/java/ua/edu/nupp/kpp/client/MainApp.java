package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private final HttpEquipmentRentalClient api = new HttpEquipmentRentalClient();
    private final TableView<Client> clients = new TableView<>();
    private final TableView<Equipment> equipment = new TableView<>();
    private final TableView<RentalRequest> requests = new TableView<>();
    private final TableView<RentalContract> contracts = new TableView<>();

    @Override public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Клієнти", clientPane()),
                new Tab("Обладнання", equipmentPane()),
                new Tab("Запити на прокат", requestPane()),
                new Tab("Договори прокату", contractPane())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));
        stage.setScene(new Scene(tabs, 1100, 680));
        stage.setTitle("Система обліку прокату обладнання");
        stage.show();
        refreshAll();
    }

    private Pane clientPane() {
        clients.getColumns().setAll(col("ID", "id"), col("ПІБ", "fullName"), col("Телефон", "phone"), col("Email", "email"));
        TextField fullName = new TextField(), phone = new TextField(), email = new TextField();
        Button add = new Button("Додати"), update = new Button("Змінити"), delete = new Button("Видалити"), refresh = new Button("Оновити");
        add.setOnAction(e -> safe(() -> { api.addClient(new Client(null, fullName.getText(), phone.getText(), email.getText())); refreshClients(); }));
        update.setOnAction(e -> safe(() -> { Client c = selected(clients); c.setFullName(fullName.getText()); c.setPhone(phone.getText()); c.setEmail(email.getText()); api.updateClient(c); refreshClients(); }));
        delete.setOnAction(e -> safe(() -> { api.deleteClient(selected(clients).getId()); refreshAll(); }));
        refresh.setOnAction(e -> refreshClients());
        clients.getSelectionModel().selectedItemProperty().addListener((o, a, c) -> { if(c != null) { fullName.setText(c.getFullName()); phone.setText(c.getPhone()); email.setText(c.getEmail()); } });
        return layout(clients, new VBox(8, new Label("ПІБ"), fullName, new Label("Телефон"), phone, new Label("Email"), email, new HBox(8, add, update, delete, refresh)));
    }

    private Pane equipmentPane() {
        equipment.getColumns().setAll(col("ID", "id"), col("Назва", "name"), col("Категорія", "category"), col("Тариф за день", "rentalRate"));
        TextField name = new TextField(), category = new TextField(), rentalRate = new TextField();
        Button add = new Button("Додати"), update = new Button("Змінити"), delete = new Button("Видалити"), refresh = new Button("Оновити");
        Button importCsv = new Button("Імпортувати CSV"), cancelImport = new Button("Скасувати");
        cancelImport.setDisable(true);
        ProgressBar progress = new ProgressBar(0);
        Label status = new Label("CSV не запущено");
        final ImportEquipmentTask[] currentTask = new ImportEquipmentTask[1];

        add.setOnAction(e -> safe(() -> { api.addEquipment(readEquipment(null, name, category, rentalRate)); refreshEquipment(); }));
        update.setOnAction(e -> safe(() -> { Equipment eq = selected(equipment); api.updateEquipment(readEquipment(eq.getId(), name, category, rentalRate)); refreshEquipment(); }));
        delete.setOnAction(e -> safe(() -> { api.deleteEquipment(selected(equipment).getId()); refreshAll(); }));
        refresh.setOnAction(e -> refreshEquipment());
        equipment.getSelectionModel().selectedItemProperty().addListener((o, a, eq) -> { if (eq != null) { name.setText(eq.getName()); category.setText(eq.getCategory()); rentalRate.setText("" + eq.getRentalRate()); } });

        cancelImport.setOnAction(e -> {
            if (currentTask[0] != null) {
                currentTask[0].cancel();
            }
        });

        importCsv.setOnAction(e -> {
            ImportEquipmentTask task = new ImportEquipmentTask(Path.of("sample-data", "equipment.csv"), api);
            currentTask[0] = task;
            progress.progressProperty().bind(task.progressProperty());
            status.textProperty().bind(task.messageProperty());
            importCsv.setDisable(true);
            cancelImport.setDisable(false);

            task.setOnSucceeded(x -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                importCsv.setDisable(false);
                cancelImport.setDisable(true);
                currentTask[0] = null;
                if (task.getProcessed() == 0 && task.getSkipped() == 0) {
                    status.setText("Немає рядків для імпорту");
                } else {
                    status.setText("Готово. Опрацьовано: " + task.getProcessed() + ", імпортовано: " + task.getValue() + ", пропущено: " + task.getSkipped());
                }
                refreshEquipment();
            });

            task.setOnFailed(x -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                importCsv.setDisable(false);
                cancelImport.setDisable(true);
                currentTask[0] = null;

                Throwable ex = task.getException();
                LOG.log(java.util.logging.Level.WARNING, "Помилка під час CSV-імпорту", ex);

                String reason = (ex != null && ex.getMessage() != null && !ex.getMessage().isBlank())
                        ? ex.getMessage()
                        : "Сервер недоступний або сталася помилка імпорту";
                status.setText("Помилка: " + reason + " (перевірте БД — частина записів могла зберегтися)");
                showError(new RuntimeException(reason));
                refreshEquipment();
            });

            task.setOnCancelled(x -> {
                progress.progressProperty().unbind();
                status.textProperty().unbind();
                importCsv.setDisable(false);
                cancelImport.setDisable(true);
                currentTask[0] = null;

                status.setText("Імпорт скасовано; частина записів могла бути збережена");
                refreshEquipment();
            });
            Thread thread = new Thread(task, "csv-import");
            thread.setDaemon(true);
            thread.start();
        });

        VBox form = new VBox(6,
                new Label("Назва"), name,
                new Label("Категорія"), category,
                new Label("Тариф за день"), rentalRate,
                new HBox(8, add, update, delete, refresh),
                new HBox(8, importCsv, cancelImport),
                progress, status);
        return layout(equipment, form);
    }

    private Pane requestPane() {
        requests.getColumns().setAll(col("ID", "id"), col("ID Клієнта", "clientId"), col("ID Обладнання", "equipmentId"), col("Стан", "state"), col("F1", "f1"), col("F2", "f2"), col("F3", "f3"), col("Інтегральний індекс", "integralIndex"));
        TextField clientId = new TextField(), equipmentId = new TextField(), f1 = new TextField(), f2 = new TextField(), f3 = new TextField();
        ComboBox<RentalRequestState> stateCombo = new ComboBox<>(FXCollections.observableArrayList(RentalRequestState.values()));
        Button add = new Button("Створити"), update = new Button("Змінити"), changeState = new Button("Змінити стан"), delete = new Button("Видалити"), refresh = new Button("Оновити");
        add.setOnAction(e -> safe(() -> { api.addRentalRequest(readRequest(null, clientId, equipmentId, f1, f2, f3)); refreshRequests(); }));
        update.setOnAction(e -> safe(() -> { RentalRequest r = selected(requests); api.updateRentalRequest(readRequest(r.getId(), clientId, equipmentId, f1, f2, f3)); refreshRequests(); }));
        changeState.setOnAction(e -> safe(() -> { api.changeRequestState(selected(requests).getId(), stateCombo.getValue()); refreshRequests(); }));
        delete.setOnAction(e -> safe(() -> { api.deleteRentalRequest(selected(requests).getId()); refreshAll(); }));
        refresh.setOnAction(e -> refreshRequests());
        requests.getSelectionModel().selectedItemProperty().addListener((o, a, r) -> { if(r != null) { clientId.setText("" + r.getClientId()); equipmentId.setText("" + r.getEquipmentId()); f1.setText("" + r.getF1()); f2.setText("" + r.getF2()); f3.setText("" + r.getF3()); stateCombo.setValue(r.getState()); } });
        return layout(requests, new VBox(8, new Label("ID Клієнта"), clientId, new Label("ID Обладнання"), equipmentId, new Label("F1"), f1, new Label("F2"), f2, new Label("F3"), f3, new Label("Новий стан"), stateCombo, new HBox(8, add, update, changeState, delete, refresh)));
    }

    private Pane contractPane() {
        contracts.getColumns().setAll(col("ID", "id"), col("ID Запиту", "requestId"), col("Дата договору", "contractDate"), col("Загальна вартість", "totalCost"));
        TextField requestId = new TextField(), contractDate = new TextField(), totalCost = new TextField();
        Button add = new Button("Додати"), update = new Button("Змінити"), delete = new Button("Видалити"), refresh = new Button("Оновити");
        add.setOnAction(e -> safe(() -> { api.addRentalContract(readContract(null, requestId, contractDate, totalCost)); refreshContracts(); }));
        update.setOnAction(e -> safe(() -> { RentalContract rc = selected(contracts); api.updateRentalContract(readContract(rc.getId(), requestId, contractDate, totalCost)); refreshContracts(); }));
        delete.setOnAction(e -> safe(() -> { api.deleteRentalContract(selected(contracts).getId()); refreshAll(); }));
        refresh.setOnAction(e -> refreshContracts());
        contracts.getSelectionModel().selectedItemProperty().addListener((o, a, rc) -> { if(rc != null) { requestId.setText("" + rc.getRequestId()); contractDate.setText(rc.getContractDate()); totalCost.setText("" + rc.getTotalCost()); } });
        return layout(contracts, new VBox(8, new Label("ID Запиту"), requestId, new Label("Дата договору YYYY-MM-DD"), contractDate, new Label("Загальна вартість"), totalCost, new HBox(8, add, update, delete, refresh)));
    }

    private Equipment readEquipment(Long id, TextField name, TextField category, TextField rentalRate) {
        return new Equipment(id, name.getText(), category.getText(), Double.parseDouble(rentalRate.getText()));
    }

    private RentalRequest readRequest(Long id, TextField clientId, TextField equipmentId, TextField f1, TextField f2, TextField f3) {
        return new RentalRequest(id, Long.parseLong(clientId.getText()), Long.parseLong(equipmentId.getText()), RentalRequestState.CREATED, Double.parseDouble(f1.getText()), Double.parseDouble(f2.getText()), Double.parseDouble(f3.getText()), 0.0);
    }

    private RentalContract readContract(Long id, TextField requestId, TextField contractDate, TextField totalCost) {
        return new RentalContract(id, Long.parseLong(requestId.getText()), contractDate.getText(), Double.parseDouble(totalCost.getText()));
    }

    private void refreshAll() { refreshClients(); refreshEquipment(); refreshRequests(); refreshContracts(); }
    private void refreshClients() { safe(() -> clients.setItems(FXCollections.observableArrayList(api.getClients()))); }
    private void refreshEquipment() { safe(() -> equipment.setItems(FXCollections.observableArrayList(api.getEquipmentList()))); }
    private void refreshRequests() { safe(() -> requests.setItems(FXCollections.observableArrayList(api.getRentalRequests()))); }
    private void refreshContracts() { safe(() -> contracts.setItems(FXCollections.observableArrayList(api.getRentalContracts()))); }
    private <T> T selected(TableView<T> table) { T v = table.getSelectionModel().getSelectedItem(); if(v == null) throw new IllegalArgumentException("Виберіть запис"); return v; }
    private void safe(Runnable action) { try { action.run(); } catch(Exception e) { LOG.warning(e.getMessage()); showError(e); } }
    private void showError(Throwable e) { Alert d = new Alert(Alert.AlertType.ERROR, e == null ? "Помилка" : e.getMessage(), ButtonType.OK); d.showAndWait(); }
    private Pane layout(TableView<?> table, Pane form) { SplitPane p = new SplitPane(table, form); p.setDividerPositions(0.68); BorderPane root = new BorderPane(p); root.setPadding(new Insets(10)); return root; }
    private <S, T> TableColumn<S, T> col(String title, String property) { TableColumn<S, T> c = new TableColumn<>(title); c.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>(property)); return c; }
    public static void main(String[] args) { launch(args); }
}
package org.example.ingatlankezelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;  // Importálás
import java.util.ArrayList;  // Importálás

public class HelloController {

    @FXML
    private TableView<Property> propertyTable;
    @FXML
    private TextField addressField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField rentPriceField;
    @FXML
    private TextField statusField;
    @FXML
    private Button addButton;

    private final ObservableList<Property> propertyList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Define table columns
        TableColumn<Property, String> addressColumn = new TableColumn<>("Cím");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Property, String> typeColumn = new TableColumn<>("Típus");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Property, Double> rentPriceColumn = new TableColumn<>("Bérleti díj");
        rentPriceColumn.setCellValueFactory(new PropertyValueFactory<>("rentPrice"));

        TableColumn<Property, String> statusColumn = new TableColumn<>("Állapot");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        propertyTable.getColumns().addAll(addressColumn, typeColumn, rentPriceColumn, statusColumn);
        propertyTable.setItems(propertyList);

        // Add event listeners
        addButton.setOnAction(event -> addProperty());
        propertyTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to edit
                editProperty();
            }
        });

        // Load properties from the database
        loadPropertiesFromDatabase();
    }

    private void loadPropertiesFromDatabase() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        List<Property> properties = dbConnection.getAllProperties();
        propertyList.setAll(properties);  // Add all properties to the ObservableList
    }

    private void addProperty() {
        String address = addressField.getText();
        String type = typeField.getText();
        String rentPriceText = rentPriceField.getText();
        String status = statusField.getText();

        if (address.isEmpty() || type.isEmpty() || rentPriceText.isEmpty() || status.isEmpty()) {
            showAlert("Hiba", "Minden mezőt ki kell tölteni!", Alert.AlertType.ERROR);
            return;
        }

        try {
            double rentPrice = Double.parseDouble(rentPriceText);
            Property newProperty = new Property(address, type, rentPrice, status);

            // Save the new property to the database
            DatabaseConnection dbConnection = new DatabaseConnection();
            boolean success = dbConnection.saveToDatabase(newProperty);

            if (success) {
                propertyList.add(newProperty);  // Add to the ObservableList if successful
                clearFields();
            } else {
                showAlert("Hiba", "Hiba történt az ingatlan mentésekor!", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Hiba", "A bérleti díjnak számnak kell lennie!", Alert.AlertType.ERROR);
        }
    }

    private void editProperty() {
        Property selectedProperty = propertyTable.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            showAlert("Figyelmeztetés", "Válassz ki egy ingatlant a szerkesztéshez!", Alert.AlertType.WARNING);
            return;
        }

        // Populate fields with the selected property data
        addressField.setText(selectedProperty.getAddress());
        typeField.setText(selectedProperty.getType());
        rentPriceField.setText(String.valueOf(selectedProperty.getRentPrice()));
        statusField.setText(selectedProperty.getStatus());

        // Update property when the Add button is clicked
        addButton.setText("Szerkesztés");
        addButton.setOnAction(event -> updateProperty(selectedProperty));
    }

    private void updateProperty(Property property) {
        try {
            property.setAddress(addressField.getText());
            property.setType(typeField.getText());
            property.setRentPrice(Double.parseDouble(rentPriceField.getText()));
            property.setStatus(statusField.getText());

            // Update the property in the database
            DatabaseConnection dbConnection = new DatabaseConnection();
            boolean success = dbConnection.saveToDatabase(property);

            if (success) {
                propertyTable.refresh();  // Refresh table view if successful
                clearFields();
                addButton.setText("Hozzáadás");
                addButton.setOnAction(event -> addProperty());
            } else {
                showAlert("Hiba", "Hiba történt az ingatlan frissítésekor!", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Hiba", "A bérleti díjnak számnak kell lennie!", Alert.AlertType.ERROR);
        }
    }

    private void clearFields() {
        addressField.clear();
        typeField.clear();
        rentPriceField.clear();
        statusField.clear();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

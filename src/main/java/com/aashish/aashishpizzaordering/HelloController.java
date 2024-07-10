package com.aashish.aashishpizzaordering;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class HelloController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField mobileField;

    @FXML
    private CheckBox xlBox;

    @FXML
    private CheckBox lBox;

    @FXML
    private CheckBox mBox;

    @FXML
    private CheckBox sBox;

    @FXML
    private Spinner<Integer> toppingsSpinner;

    @FXML
    private TableView<Pizza> orderTable;

    private ObservableList<Pizza> orders = FXCollections.observableArrayList();

    private final String url = "jdbc:mysql://localhost:3306/pizza_orders";
    private final String user = "yourUsername"; // Replace with your MySQL username
    private final String password = "yourPassword"; // Replace with your MySQL password

    @FXML
    private void initialize() {
        // Initialize TableView columns
        TableColumn<Pizza, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Pizza, String> nameCol = new TableColumn<>("Customer Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Pizza, String> mobileCol = new TableColumn<>("Mobile Number");
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobileNumber"));

        TableColumn<Pizza, String> sizeCol = new TableColumn<>("Pizza Size");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));

        TableColumn<Pizza, Integer> toppingsCol = new TableColumn<>("Number of Toppings");
        toppingsCol.setCellValueFactory(new PropertyValueFactory<>("toppings"));

        TableColumn<Pizza, Double> totalBillCol = new TableColumn<>("Total Bill");
        totalBillCol.setCellValueFactory(new PropertyValueFactory<>("totalBill"));

        orderTable.getColumns().addAll(idCol, nameCol, mobileCol, sizeCol, toppingsCol, totalBillCol);
        orderTable.setItems(orders);

        // Load existing orders from database
        loadOrders();
    }

    @FXML
    private void handleAdd() {
        String size = xlBox.isSelected() ? "XL" : lBox.isSelected() ? "L" : mBox.isSelected() ? "M" : sBox.isSelected() ? "S" : "";
        int toppings = toppingsSpinner.getValue();
        double totalBill = calculateTotalBill(size, toppings);

        Pizza pizza = new Pizza(-1, nameField.getText(), mobileField.getText(), size, toppings, totalBill);
        insertPizza(pizza);
        orders.add(pizza);
        clearFields();
    }

    @FXML
    private void handleUpdate() {
        Pizza selectedPizza = orderTable.getSelectionModel().getSelectedItem();
        if (selectedPizza != null) {
            String size = xlBox.isSelected() ? "XL" : lBox.isSelected() ? "L" : mBox.isSelected() ? "M" : sBox.isSelected() ? "S" : "";
            int toppings = toppingsSpinner.getValue();
            double totalBill = calculateTotalBill(size, toppings);

            selectedPizza.setCustomerName(nameField.getText());
            selectedPizza.setMobileNumber(mobileField.getText());
            selectedPizza.setSize(size);
            selectedPizza.setToppings(toppings);
            selectedPizza.setTotalBill(totalBill);

            updatePizza(selectedPizza);
            orderTable.refresh();
            clearFields();
        }
    }

    @FXML
    private void handleDelete() {
        Pizza selectedPizza = orderTable.getSelectionModel().getSelectedItem();
        if (selectedPizza != null) {
            deletePizza(selectedPizza);
            orders.remove(selectedPizza);
            clearFields();
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
    }

    @FXML
    private void handleLoad() {
        loadOrders();
    }

    private void clearFields() {
        nameField.clear();
        mobileField.clear();
        xlBox.setSelected(false);
        lBox.setSelected(false);
        mBox.setSelected(false);
        sBox.setSelected(false);
        toppingsSpinner.getValueFactory().setValue(0);
    }

    private double calculateTotalBill(String size, int toppings) {
        double basePrice;
        switch (size) {
            case "XL":
                basePrice = 15.00;
                break;
            case "L":
                basePrice = 12.00;
                break;
            case "M":
                basePrice = 10.00;
                break;
            case "S":
                basePrice = 8.00;
                break;
            default:
                basePrice = 0.00;
        }

        double toppingCost = 1.50 * toppings;
        double subTotal = basePrice + toppingCost;
        double hst = 0.15 * subTotal;
        return subTotal + hst;
    }

    private void loadOrders() {
        orders.clear();
        String sql = "SELECT * FROM pizza_orders";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String customerName = rs.getString("customerName");
                String mobileNumber = rs.getString("mobileNumber");
                String size = rs.getString("pizzaSize");
                int toppings = rs.getInt("numberOfToppings");
                double totalBill = rs.getDouble("totalBill");

                Pizza pizza = new Pizza(id, customerName, mobileNumber, size, toppings, totalBill);
                orders.add(pizza);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertPizza(Pizza pizza) {
        String sql = "INSERT INTO pizza_orders (customerName, mobileNumber, pizzaSize, numberOfToppings, totalBill) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, pizza.getCustomerName());
            pstmt.setString(2, pizza.getMobileNumber());
            pstmt.setString(3, pizza.getSize());
            pstmt.setInt(4, pizza.getToppings());
            pstmt.setDouble(5, pizza.getTotalBill());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        pizza.setId(id);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePizza(Pizza pizza) {
        String sql = "UPDATE pizza_orders SET customerName = ?, mobileNumber = ?, pizzaSize = ?, numberOfToppings = ?, totalBill = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pizza.getCustomerName());
            pstmt.setString(2, pizza.getMobileNumber());
            pstmt.setString(3, pizza.getSize());
            pstmt.setInt(4, pizza.getToppings());
            pstmt.setDouble(5, pizza.getTotalBill());
            pstmt.setInt(6, pizza.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deletePizza(Pizza pizza) {
        String sql = "DELETE FROM pizza_orders WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pizza.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

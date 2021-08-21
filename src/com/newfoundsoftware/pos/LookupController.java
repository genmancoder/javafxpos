/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newfoundsoftware.pos;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Admin
 */
public class LookupController implements Initializable {

    @FXML
    private TableView<Products> tableProducts;
    @FXML
    private TableColumn<Products, Integer> colId;
    @FXML
    private TableColumn<Products, String> colDescription;
    @FXML
    private TableColumn<Products, String> colPrice;
    @FXML
    private TableColumn<Products, String> colCategory;
    @FXML
    private TableColumn<Products, String> colStatus;
    JdbcDao jdbc;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        jdbc = new JdbcDao();
        showProducts();
    }

    public void showProducts() {
        ObservableList<Products> list = getProductList();
        colId.setCellValueFactory(new PropertyValueFactory<Products, Integer>("id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<Products, String>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Products, String>("price"));
        colCategory.setCellValueFactory(new PropertyValueFactory<Products, String>("category"));
        colStatus.setCellValueFactory(new PropertyValueFactory<Products, String>("status"));

        tableProducts.setItems(list);
    }

    private ObservableList<Products> getProductList() {
        ObservableList<Products> productList = FXCollections.observableArrayList();
        Connection conn = jdbc.getConnection();
        String query = "SELECT * FROM products";
        Statement st;
        ResultSet rs;

        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Products products;
            while (rs.next()) {
                products = new Products(rs.getInt("id"), rs.getString("barcode"), rs.getString("description"), rs.getString("price"), rs.getString("category"), rs.getBlob("image"), rs.getString("status"));
                productList.add(products);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return productList;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newfoundsoftware.pos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author Admin
 */
public class ProductsController implements Initializable {

    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private TableView<Products> tableProducts;
    @FXML
    private TableColumn<Products, Integer> colId;
    private TableColumn<Products, String> colName;
    @FXML
    private ImageView ivProduct;
    @FXML
    private TextField etId;
    @FXML
    private TextField etDescription;
    @FXML
    private TextField etPrice;
    @FXML
    private ComboBox<String> cbCategories;
    @FXML
    private Button btnSave1;
    @FXML
    private ComboBox<String> cbWeight;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private Button btnSave;

    JdbcDao jdbc;
    @FXML
    private TableColumn<Products, String> colDescription;
    @FXML
    private TableColumn<Products, String> colPrice;
    @FXML
    private TableColumn<Products, String> colCategory;
    @FXML
    private TableColumn<Products, String> colStatus;

    Scene fxmlFile;
    Parent root;
    Stage window;

    File file;

    @FXML
    private Button btnBrowse;
    @FXML
    private TextField etBarcode;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        jdbc = new JdbcDao();
        addListenerForTable();
        showProducts();
        populateCategories();

        cbStatus.getItems().add("AVAILABLE");
        cbStatus.getItems().add("UNAVAILABLE");

        cbWeight.getItems().add("YES");
        cbWeight.getItems().add("NO");

    }

    @FXML
    private void editEntry(ActionEvent event) {
            Connection conn = jdbc.getConnection();
        try{
            Products product = tableProducts.getSelectionModel().getSelectedItem();
            String query = "UPDATE products SET description = '" + etDescription.getText() + "', "
                    + "price = '" + etPrice.getText() +"', category = '" + 
                    cbCategories.getSelectionModel().getSelectedItem() + "', status = '" + cbStatus.getSelectionModel().getSelectedItem() + 
                    "' WHERE id = '" + product.getId() + "'";
            executeQuery(query);
            showProducts();
            
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void deleteEntry(ActionEvent event) {
           Connection conn = jdbc.getConnection();
        try{
            Products product = tableProducts.getSelectionModel().getSelectedItem();
            String query = "DELETE FROM products WHERE id = '" + product.getId() + "'";
            executeQuery(query);
            showProducts();
            
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
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

    private void insertRecord() {
        //String name = tfTableName.getText();
//        if(!name.isEmpty()){
//            String query = "INSERT INTO `tbltables` (name) VALUES('" + name + "')";
//            executeQuery(query);
//            showProducts();
//           
//        }
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

    private void executeQuery(String query) {
        Connection conn = jdbc.getConnection();
        Statement st;
        System.out.println(query);
        try {
            st = conn.createStatement();
            st.executeUpdate(query);
        } catch (Exception ex) {
            System.out.println("error while inserting record.");
            ex.printStackTrace();
        }
    }

    private void openModalWindow(String resource, String title) throws IOException {
        root = FXMLLoader.load(getClass().getResource(resource));
        fxmlFile = new Scene(root);
        window = new Stage();
        window.setScene(fxmlFile);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setAlwaysOnTop(true);
        window.setIconified(false);
//        window.initStyle(StageStyle.UNDECORATED);
        window.setTitle(title);
        window.showAndWait();
    }

    @FXML
    private void actionAddCategory(ActionEvent event) {
        try {
            openModalWindow("Category.fxml", "Manage Categories");
        } catch (Exception ex) {

        }
    }

    private void populateCategories() {
        Connection conn = jdbc.getConnection();
        Statement st;
        //System.out.println(query);
        ObservableList<String> list = FXCollections.observableArrayList();
        try {
            //st = conn.createStatement();
            //st.executeUpdate(query);

            ResultSet rs = conn.createStatement().executeQuery("select * from categories");
            while (rs.next()) {
                list.add(rs.getString("name"));
//              cbCategories.add(rs.getString("name"));
            }

        } catch (Exception ex) {
            System.out.println("error while inserting record.");
            ex.printStackTrace();
        }

        cbCategories.setItems(null);
        cbCategories.setItems(list);

    }

    private void addListenerForTable() {
        tableProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                btnSave.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
//                tfCategoryName.setText(newSelection.getName());                
                etId.setText("" + newSelection.getId());
                etBarcode.setText(newSelection.getBarcode());
                etDescription.setText(newSelection.getDescription());
                etPrice.setText(newSelection.getPrice());
                
                cbCategories.getSelectionModel().select(newSelection.getCategory());
                cbStatus.getSelectionModel().select(newSelection.getStatus());
//                cbWeight.getSelectionModel().select(newSelection.get);
            } else {
                etBarcode.setText("");
                etDescription.setText("");
                etPrice.setText("");
                cbCategories.getSelectionModel().selectFirst();
                cbStatus.getSelectionModel().selectFirst();
                
                btnSave.setDisable(false);
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
            }
        });
    }

    @FXML
    private void handleBrowseImage(ActionEvent event) {
        try {
            FileChooser fc = new FileChooser();
            FileChooser.ExtensionFilter ext1 = new FileChooser.ExtensionFilter("JPG files(*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter ext2 = new FileChooser.ExtensionFilter("PNG files(*.png)", "*.PNG");

            fc.getExtensionFilters().addAll(ext1, ext2);

            file = fc.showOpenDialog(DashboardController.getPrimaryStage());
            BufferedImage bf;
            bf = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bf, null);
            ivProduct.setImage(image);

        } catch (Exception ex) {
            System.out.println("" + ex.getMessage());
        }
    }

    @FXML
    private void saveProduct(ActionEvent event) {
        Connection conn = jdbc.getConnection();
        try {
            String barcode = etBarcode.getText();
            String description = etDescription.getText();
            String price = etPrice.getText();
            String category = cbCategories.getSelectionModel().getSelectedItem();
            String isweight = cbWeight.getSelectionModel().getSelectedItem();
            String status = cbStatus.getSelectionModel().getSelectedItem();
            Window owner = (Stage) etDescription.getScene().getWindow();
            if (barcode.isEmpty() || description.isEmpty() || price.isEmpty() || category.isEmpty()
                    || isweight.isEmpty() || status.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, owner, "Please fillup the form correctly.", "Form Error!");
            } else {
                //check if the user selected an image
                if (file == null) {
                    showAlert(Alert.AlertType.ERROR, owner, "Please select an image.", "Form Error!");
                } else {
                    FileInputStream fin = new FileInputStream(file);
                    int len = (int) file.length();
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO products(barcode,description,price,category,weight,image,status) VALUES(?,?,?,?,?,?,?)");
                    ps.setString(1, barcode);
                    ps.setString(2, description);
                    ps.setString(3, price);
                    ps.setString(4, category);
                    ps.setString(5, isweight);
                    ps.setBinaryStream(6, fin, len);
                    ps.setString(7, status);

                    int res = ps.executeUpdate();
                    if (res > 0) {
                        showAlert(Alert.AlertType.INFORMATION, owner, "Products saved successfully.", "Success!");

                        etBarcode.clear();
                        etDescription.clear();
                        etPrice.clear();

                        cbCategories.valueProperty().set(null);
                        cbWeight.valueProperty().set(null);
                        cbStatus.valueProperty().set(null);

                        ivProduct.setImage(null);
                        file = null;
                        showProducts();
                    } else {
                        showAlert(Alert.AlertType.ERROR, owner, "There were errors while processing.", "Form Error!");
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("" + ex.getMessage());
        }
    }

    public static void showAlert(Alert.AlertType alertType, Window owner, String message, String title) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(owner);
        alert.showAndWait();
    }

}

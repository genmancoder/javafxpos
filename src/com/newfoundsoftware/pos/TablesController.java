/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newfoundsoftware.pos;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
/**
 * FXML Controller class
 *
 * @author Admin
 */
public class TablesController implements Initializable {

    @FXML
    private TextField tfTableName;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    private TableView<Tables> tableTables;
    @FXML
    private TableColumn<Tables, Integer> colId;
    @FXML
    private TableColumn<Tables, String> colName;

    JdbcDao jdbc;
    @FXML
    private TableView<Tables> tvTables;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        
        jdbc = new JdbcDao();
        showTable();
        addListenerForTable();
        
    } 
    
    public void showTable(){
        ObservableList<Tables> list = getTableList();
        colId.setCellValueFactory(new PropertyValueFactory<Tables, Integer>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<Tables, String>("name"));        
        tvTables.setItems(list);
    }
    
    private void insertRecord(){
        String name = tfTableName.getText();
        if(!name.isEmpty()){
            String query = "INSERT INTO `tbltables` (name) VALUES('" + name + "')";
            executeQuery(query);
            showTable();
            tfTableName.setText("");
        }
    }

    private ObservableList<Tables> getTableList() {
     ObservableList<Tables> tableList = FXCollections.observableArrayList();
     Connection conn = jdbc.getConnection();
     String query = "SELECT * FROM tbltables";
     Statement st;
     ResultSet rs;
     
     try{
         st = conn.createStatement();
         rs = st.executeQuery(query);
         Tables tables;
         while(rs.next()){
             tables = new Tables(rs.getInt("id"), rs.getString("name"));
             tableList.add(tables);
         }
     }catch(Exception ex){
         System.out.println(ex.getMessage());
     }
     
     return tableList;
    }

    private void executeQuery(String query) {
        Connection conn = jdbc.getConnection();
        Statement st;
        System.out.println(query);
        try{
            st = conn.createStatement();
            st.executeUpdate(query);
        }catch(Exception ex){
            System.out.println("error while inserting record.");
            ex.printStackTrace();
        } 
    }

    @FXML
    private void saveTable(ActionEvent event) {
        insertRecord();
    }
    
    private void addListenerForTable(){
        tvTables.getSelectionModel().selectedItemProperty().addListener((obs,oldSelection,newSelection) -> {
            if(newSelection != null){
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);                
                tfTableName.setText(newSelection.getName());                
            }else{
                tfTableName.setText("");
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
            }
        });
    }

    @FXML
    private void editEntry(ActionEvent event) {
        Connection conn = jdbc.getConnection();
        try{
            Tables table = tableTables.getSelectionModel().getSelectedItem();
            String query = "UPDATE tbltables SET name = '" + tfTableName.getText() + " ' WHERE id = '" + table.getId() + "'";
            executeQuery(query);
            showTable();
            
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void deleteEntry(ActionEvent event) {
         Connection conn = jdbc.getConnection();
        try{
            Tables table = tableTables.getSelectionModel().getSelectedItem();
            String query = "DELETE FROM tbltables WHERE id = '" + table.getId() + "'";
            executeQuery(query);
            showTable();
            
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    
}

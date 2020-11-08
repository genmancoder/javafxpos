package com.newfoundsoftware.pos;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Admin
 */
public class Login extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
       try{
           
           Parent parentRoot = FXMLLoader.load(getClass().getResource("Login.fxml"));
           primaryStage.setTitle("Login");
           primaryStage.setResizable(false);
           primaryStage.setIconified(false);
           primaryStage.setScene(new Scene(parentRoot));
           primaryStage.show();
           
       }catch(Exception ex){
           System.err.println(ex.getMessage());
           ex.printStackTrace();
           System.exit(0);
       }
    }
    
    public static void main(String[]args){
        launch(args);
    }
    
}

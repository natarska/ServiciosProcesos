/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psp.michat_client;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dirrospace
 */


public class WindowChatController extends Application {

    @FXML
    private TextField _text;
    

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/windowChat.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setTitle("Chats");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
// WINDOWS.MODAL = 
//      - if windowmodal = dejarte interactuar con la anterior ventana

    @FXML
    private void onEnviar(ActionEvent event) {
    }


} 
    


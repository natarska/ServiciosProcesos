/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psp.michat_client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleRole;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.cookie.Cookie;
import proxy.Proxy;

/**
 * FXML Controller class
 *
 * @author dirrospace
 */


public class WindowChatController extends Application {
    private CloseableHttpClient httpclient = null;
    private HttpClientContext context  = null;
    private Proxy proxy = null;
    
    
    @FXML
    private TextField _text;
    @FXML
    private Button btnEnvio;
    @FXML
    private Button btnLogin;
    
    public  WindowChatController (){
        btnEnvio.setDisable(true);
        httpclient = HttpClients.createDefault();
        context = HttpClientContext.create();
    }
    
    private String getSessionFromContext(HttpClientContext context) {
        String sessionId = "";
        for(Cookie cookie :  context.getCookieStore().getCookies())
        {
            if (cookie.getName().equals("JSESSIONID"))
                sessionId = cookie.getValue();
                
        }
        return sessionId;
    }
    
    @FXML
    private void onEnviar(ActionEvent event) {
    }

    @FXML
    private void onLogin(ActionEvent event) {
        try {
            HttpPost httpPost = new HttpPost("http://localhost:8080/chatServer/Login");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            nvps.add(new BasicNameValuePair("user", _text.getText()));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            
            CloseableHttpResponse response2 = httpclient.execute(httpPost,context);
            HttpEntity entity = response2.getEntity();
            btnEnvio.setDisable(false);
            btnLogin.setDisable(true);
        } catch (Exception ex) {
            Logger.getLogger(WindowChatController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/windowChat.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setTitle("Chats");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

} 
    


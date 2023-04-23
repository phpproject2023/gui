package com.example.project;

import com.example.project.iterator.Iterator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class BookDetailController {

    @FXML
    private ImageView bookImage;
    @FXML
    private Text bookTitle;
    @FXML
    private Text bookAuthor;
    @FXML
    private Text bookCategory;
    @FXML
    private Text bookDescription;

    private ProxyUser proxyUser;


    private Iterator iterator;
    private String email ;

    public void backHandler(ActionEvent actionEvent) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("allbooks.fxml"));
        AnchorPane root = fxmlLoader.load();

        AllBooksController allBooksController = fxmlLoader.getController();
        allBooksController.setProxyUser(this.proxyUser);
        allBooksController.start();


        Stage stage =(Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void setAuthor(String name ){this.bookAuthor.setText(name);}
    public void setTitle(String title){this.bookTitle.setText(title);}
    public void setDescription(String description){this.bookDescription.setText(description);}
    public void setCategory(String category){this.bookCategory.setText(category);}
    public void setImage(String bookImage){
        Image image= new Image(getClass().getResourceAsStream("/"+bookImage));
        this.bookImage.setImage(image);
    }

    public void setProxyUser(ProxyUser proxyUser){
        this.proxyUser = proxyUser;
        this.email = proxyUser.getRealUser().getEmail();
    }
}

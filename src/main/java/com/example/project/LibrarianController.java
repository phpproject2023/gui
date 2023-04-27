package com.example.project;

import com.example.project.proxyUser.ProxyLibrarian;
import com.example.project.proxyUser.ProxyUser;
import com.example.project.user.Borrower;
import com.example.project.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;



public class LibrarianController {
    @FXML
    @com.example.project.FXML
    private Pane pane;
    @FXML
    private TextField bookTitle;
    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField bookAuthor;
    @FXML
    private TextField bookCat;

    @FXML
    private TextField bookDesc;

    @FXML
    private TextField bookLength;

    @FXML
    private TextField bookQuantity;

    @FXML
    private Node addBook;

    ResultSet resultSet ;
    Database database;

    {
        try {
            database = new Database();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private File selectedFile;
    public ProxyLibrarian proxyUser;
    public void Logout(ActionEvent actionEvent) throws SQLException, IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Entry.fxml"));
        Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();


        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(1350);
        stage.setHeight(810);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
    //let the librarian choose an image to upload

    public void chooseImg(ActionEvent actionEvent) {
        FileChooser fileChooser= new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        selectedFile = fileChooser.showOpenDialog(null);
    }
    //add book to the db

    public void addBook(ActionEvent actionEvent) throws SQLException{
        String title= bookTitle.getText();
        String cat= bookCat.getText();
        String desc= bookDesc.getText();
        String author= bookAuthor.getText();
        String length= bookLength.getText();
        String quantity= bookQuantity.getText();
        String imagePath = "images/books/" + selectedFile.getName();
        String destinationDirPath = "src/main/resources/images/books";

        try {
            // Create the destination directory if it does not exist
            Path destinationDir = Paths.get(destinationDirPath);
            if (!Files.exists(destinationDir)) {
                Files.createDirectories(destinationDir);
            }

            // Copy the selected file to the destination directory
            Path destinationPath = Paths.get(destinationDirPath, selectedFile.getName());
            Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to copy image: " + e.getMessage());
        }
        if(!title.isEmpty() && !author.isEmpty() && !length.isEmpty() && !cat.isEmpty() && !quantity.isEmpty() && !desc.isEmpty() && selectedFile != null){
            Book newbook= new Book(title,author,desc,cat,imagePath,length,""+proxyUser.getRealUser().getId());
            newbook.setQuantity(quantity);
            proxyUser.getRealUser().addBook(newbook);
            proxyUser.getRealUser().notifyUsers();
        }
    }
    public void cancel() {

    }
    public void addBookHandler(ActionEvent actionEvent)throws SQLException ,IOException{
        this.pane = (Pane) this.borderPane.getChildren().get(2);
        if(pane.getChildren().get(0) != addBook){
            pane.getChildren().remove(0);
            pane.getChildren().add(0,addBook);
        }
        return;

    }

    //fetchSubscribers is activated whenever i click on list of Users
    public void fetchSubscribers(ActionEvent actionEvent) throws SQLException {
        this.pane = (Pane) this.borderPane.getChildren().get(2);
        if(this.pane.getChildren().get(0)==addBook) {
            this.addBook = pane.getChildren().get(0);
            pane.getChildren().removeAll(pane.getChildren());
            VBox vBox = new VBox();

            ArrayList<User> users = database.getUsersOfLibrarian(this.proxyUser.getRealUser());
            int i = 0;
            for (; i < users.size(); i++) {
                Borrower borrower = (Borrower) users.get(i);
                Label label = new Label(borrower.getUsername());
                label.setFont(Font.font("Corbel", 16));
                label.setTextFill(Color.web("#f0824f"));
                vBox.getChildren().add(label);


                GridPane gridPane = new GridPane();
                ///////////////////////////////////////////////////
                show(gridPane,borrower);
                gridPane.setPadding(new Insets(10));
                gridPane.setHgap(15);
                gridPane.setVgap(10);

                ///////////////////////////////////////////////////
                vBox.getChildren().add(gridPane);
                vBox.setPadding(new Insets(10));
            }
            ScrollPane scrollPane = new ScrollPane(vBox);

            scrollPane.setPrefSize(pane.getWidth()-10,pane.getHeight()-30);



            pane.getChildren().add(scrollPane);
        }
        else return;

    }

    public void setProxyUser(ProxyUser proxyUser){
        this.proxyUser = (ProxyLibrarian)proxyUser;
    }

    public void show(GridPane gridPane , User user) throws SQLException {

        ResultSet resultSet1 = database.getBorrowedBooks(user);
        int i = 0 ;
        int a = gridPane.getChildren().size();
        while(i<a){
            gridPane.getChildren().remove(0);
            i++;
        }

        int columnIndex = 0;
        int rowIndex = 0;

        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(15);
        gridPane.setVgap(10);


        while (resultSet1.next()) {
            String bookTitle= resultSet1.getString("title");
            String bookDesc = resultSet1.getString("description");
            String bookImage = resultSet1.getString("image");
            String bookCat = resultSet1.getString("category");
            String bookAuth = resultSet1.getString("authorName");

            Book book = new Book(
                    bookTitle,
                    bookAuth,
                    bookDesc,
                    bookCat,
                    resultSet1.getString("id"),
                    bookImage,
                    resultSet1.getString("bookLength"),
                    resultSet1.getString("librarianId")
            );

            ImageView imageView = new ImageView();
            Image image = new Image(getClass().getResourceAsStream("/" + bookImage));
            imageView.setImage(image);
            imageView.setFitWidth(207);
            imageView.setFitHeight(300);
            Label label = new Label(bookTitle);
            label.setFont(Font.font("Corbel", 16));
            label.setTextFill(Color.web("#f0824f"));
            VBox newVbox = new VBox();
            newVbox.getChildren().add(imageView);
            newVbox.getChildren().add(label);
            newVbox.setPrefWidth(207);
            newVbox.setPrefHeight(320);
            newVbox.getStyleClass().add("book-vbox");


            gridPane.add(newVbox, columnIndex % 5, rowIndex);

            if (columnIndex % 5 == 4) {
                rowIndex++;
                columnIndex = 0;
            } else {
                columnIndex++;
            }
        }
        resultSet1.close();
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.invoicingapp.javafx;

import invoicingapp_monolithic.Company;
import invoicingapp_monolithic.ContactPerson;
import invoicingapp_monolithic.Phone;
import invoicingapp_monolithic.Users;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author frede
 */
public class ViewCreateUserController implements Initializable {

    private Users user=new Users();
    private String introUser="Datos del usuario";
    private String introCompany="Datos de la Empresa";
    private ArrayList<Company> companies=new ArrayList();
    private Stage stage;
    
    @FXML private PasswordField fieldPasswd1,fieldPasswd2 ;
    @FXML private TextField textFieldPW1,textFieldPW2,fieldUsername, 
            fieldVAT,fieldComName,fieldLegalName,fieldEmail,fieldWeb;
    @FXML private ComboBox cbCompany;
    @FXML private Label labelIntro, labelPassword,labelError;
    @FXML private GridPane paneUser,paneCompany;
    @FXML private HBox paneFootUser,paneFootCompany;
    @FXML private VBox paneCreateUser;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        companies=Company.getAllFromDB();
        
        textFieldPW1.setVisible(false);
        textFieldPW2.setVisible(false);
        labelIntro.setText(introUser);
    }
    
    @FXML protected void onPressedSeePW(){
        textFieldPW1.setText(fieldPasswd1.getText());
        fieldPasswd1.setVisible(false);
        textFieldPW1.setVisible(true);
        textFieldPW2.setText(fieldPasswd2.getText());
        fieldPasswd2.setVisible(false);
        textFieldPW2.setVisible(true);
    }
    
    @FXML protected void onReleaseSeePW(){
        textFieldPW1.setVisible(false);
        fieldPasswd1.setVisible(true);
        textFieldPW2.setVisible(false);
        fieldPasswd2.setVisible(true);
    }
    
    @FXML protected void onClicCancel(){
        Stage stage=(Stage)paneCreateUser.getScene().getWindow();
        stage.close();
    }
    
    @FXML protected void onClicNextFromUser(){
        labelIntro.setText(introCompany);
        
        if(fieldPasswd1.getText().equals(fieldPasswd2.getText())){
            user.setUserName(fieldUsername.getText());
            user.setPasswd(fieldPasswd1.getText());
        
            paneUser.setVisible(false);
            paneCompany.setVisible(true);
            paneFootUser.setVisible(false);
            paneFootCompany.setVisible(true);
            labelPassword.setText("");
        }else{
            labelPassword.setText("Las contraseñas no coinciden");
        }
    }
    
    @FXML protected void onClicSave(){
        Stage stage=(Stage)paneCreateUser.getScene().getWindow();
        
        if(user.getAddress().getStreet()==null){
            labelError.setText("Es obligatorio añadir una dirección.");
            labelError.setVisible(true);
        }else{
            user.setVatNumber(fieldVAT.getText());
            user.setComName(fieldComName.getText());
            user.setLegalName(fieldLegalName.getText());
            user.setEmail(fieldEmail.getText());
            user.setWeb(fieldWeb.getText());
            user.addToDB();
            stage.close();
        }
    }
    
    @FXML protected void onClicBackFromCompany(){
        labelIntro.setText(introUser);
        paneUser.setVisible(true);
        paneCompany.setVisible(false);
        paneFootUser.setVisible(true);
        paneFootCompany.setVisible(false);
    }
    
    @FXML protected void onClicAddAddress(){
        ViewNewAddressController controller=null;
        FXMLLoader loader=switchWindow("viewNewAddress.fxml");
        
        controller=loader.getController();
        controller.initData(user.getAddress());
    }
    
    @FXML protected void onClicAddContact(){
        ViewNewContactController controller=null;
        FXMLLoader loader=switchWindow("viewNewContact.fxml");
        ContactPerson contact=new ContactPerson();
        
        controller=loader.getController();
        controller.initData(contact);
        user.addContactPerson(contact);
    }
    
    @FXML protected void onClicAddPhone(){
        ViewNewPhoneController controller=null;
        FXMLLoader loader=switchWindow("viewNewPhone.fxml");
        Phone phone=new Phone();
        
        controller=loader.getController();
        controller.initData(phone);
        user.addPhone(phone);
    }
    
    @FXML protected void populateComboBox(){
        ObservableList<Company> companyObs =FXCollections.observableArrayList(companies);
        cbCompany.setItems(companyObs);
        
        cbCompany.setCellFactory(new Callback<ListView<Company>, ListCell<Company>>() {
            @Override
            public ListCell<Company> call(ListView<Company> p) {
                return new ListCell<Company>() {
                    @Override
                    protected void updateItem(Company item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getComName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        
        cbCompany.setButtonCell(new ListCell<Company>() {
            @Override
            protected void updateItem(Company item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getComName());
                } else {
                    setText(null);
                }
            }
        });
    }
    
    @FXML protected void getSelectionComboBox(){
        Company company=(Company) cbCompany.getSelectionModel().getSelectedItem();
        
        fieldVAT.setText(company.getVatNumber());
        fieldComName.setText(company.getComName());
        fieldLegalName.setText(company.getLegalName());
        fieldEmail.setText(company.getEmail());
        fieldWeb.setText(company.getWeb());
        
        user.setIdCompany(company.getIdCompany());
        user.setAddress(company.getAddress());
    }
    
    private FXMLLoader switchWindow(String path){
        FXMLLoader loader=new FXMLLoader();
        Parent root=null;
        Scene scene;
        loader.setLocation(getClass().getResource(path));
        try {
            root=loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ViewCreateCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        scene=new Scene(root);
        Stage newStage=new Stage();
        newStage.setScene(scene);
        newStage.show();
        
        stage=(Stage)paneCreateUser.getScene().getWindow();
        newStage.setOnHiding(event -> {
                stage.show();
            });
        
        stage.close();
        
        return loader;
    }
    
}

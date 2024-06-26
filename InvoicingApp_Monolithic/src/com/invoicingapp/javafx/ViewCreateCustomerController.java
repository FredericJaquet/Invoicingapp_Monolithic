/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.invoicingapp.javafx;

import invoicingapp_monolithic.Company;
import invoicingapp_monolithic.ContactPerson;
import invoicingapp_monolithic.CustomProv;
import invoicingapp_monolithic.Customer;
import invoicingapp_monolithic.Phone;
import invoicingapp_monolithic.Scheme;
import invoicingapp_monolithic.SchemeLine;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;


public class ViewCreateCustomerController implements Initializable {

    private String introCompany="Datos de la Empresa";
    private String introAddress="Dirección";
    private String introFiscalData="Datos fiscales";
    private String introScheme="Esquema de facturación";
    private Customer customer=new Customer();
    private ArrayList<CustomProv> companies=new ArrayList();
    private ObservableList<SchemeLine> schemeLines=FXCollections.observableArrayList();
    private Scheme scheme=new Scheme();
    private SchemeLine line=new SchemeLine();
    private Stage stage;
    
    @FXML private Label labelIntro, labelError;
    @FXML private TextField fieldVAT,fieldComName,fieldLegalName,fieldEmailCompany,fieldWeb;
    @FXML private TextField fieldStreet,fieldStNumber,fieldApt,fieldCP,fieldCity,fieldState,fieldCountry;
    @FXML private TextField fieldDefaultVAT,fieldDefaultWithholding,fieldInvoicingMethod,fieldPayMethod,fieldDuedate;
    @FXML private CheckBox cbEurope,cbEnabled;
    @FXML private ComboBox cbCompany;
    @FXML private GridPane paneCompany, paneAddress,paneFiscalData;
    @FXML private HBox paneFootCompany,paneFootAddress,paneFootFiscalData;
    @FXML private VBox paneCreateCustomer;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        companies=CustomProv.getAllCustomProvFromDB();        
        labelIntro.setText(introCompany);
    }
    
    @FXML protected void onClicCancel(ActionEvent e){
        Button source=(Button)e.getSource();
        Stage stage=(Stage)source.getScene().getWindow();
        stage.close();
    }
    
    @FXML protected void onClicNextFromCompany(){
        labelIntro.setText(introAddress);
        customer.setVatNumber(fieldVAT.getText());
        customer.setComName(fieldComName.getText());
        customer.setLegalName(fieldLegalName.getText());
        customer.setEmail(fieldEmailCompany.getText());
        customer.setWeb(fieldWeb.getText());
        
        paneCompany.setVisible(false);
        paneFootCompany.setVisible(false);
        paneAddress.setVisible(true);
        paneFootAddress.setVisible(true);
    }
    
    @FXML protected void onClicNextFromAddress(){
        labelIntro.setText(introFiscalData);
        customer.getAddress().setStreet(fieldStreet.getText());
        customer.getAddress().setStNumber(fieldStNumber.getText());
        customer.getAddress().setApt(fieldApt.getText());
        customer.getAddress().setCp(fieldCP.getText());
        customer.getAddress().setCity(fieldCity.getText());
        customer.getAddress().setState(fieldState.getText());
        customer.getAddress().setCountry(fieldCountry.getText());
        
        paneAddress.setVisible(false);
        paneFootAddress.setVisible(false);
        paneFiscalData.setVisible(true);
        paneFootFiscalData.setVisible(true);
    }
    
    @FXML protected void onClicSaveFromFiscalData(){
        stage=(Stage)paneCreateCustomer.getScene().getWindow();
        double defaultVAT=0,defaultWithholding=0;
        int duedate=0;
        boolean control=true;
        
        try{
            defaultVAT=Double.parseDouble(fieldDefaultVAT.getText());
        }catch(NumberFormatException ex){
            fieldDefaultVAT.getStyleClass().add("error");
            control=false;
        }
        try{
            defaultWithholding=Double.parseDouble(fieldDefaultWithholding.getText());
        }catch(NumberFormatException ex){
            fieldDefaultWithholding.getStyleClass().add("error");
            control=false;
        }
        try{
            duedate=Integer.parseInt(fieldDuedate.getText());
        }catch(NumberFormatException ex){
            fieldDuedate.getStyleClass().add("error");
            control=false;
        }
        
        if(!control){
            labelError.setVisible(true);
        }else{
            customer.setDefaultVAT(defaultVAT);
            customer.setDefaultWithholding(defaultWithholding);
            customer.setEurope(cbEurope.isSelected());
            customer.setEnabled(cbEnabled.isSelected());
            customer.setInvoicingMethod(fieldInvoicingMethod.getText());
            customer.setPayMethod(fieldPayMethod.getText());
            customer.setDuedate(duedate);
            customer.addToDB();
        
            stage.close();
        }
    }
    
    @FXML protected void onClicAddContact(){
        ContactPerson contact=new ContactPerson();
        FXMLLoader loader=new FXMLLoader();
        Parent root=null;
        Scene scene;
        ViewNewContactController controller=null;
        
        loader.setLocation(getClass().getResource("viewNewContact.fxml"));
        try {
            root=loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ViewCreateCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        controller=loader.getController();
        controller.initData(contact);
        customer.addContactPerson(contact);
        
        scene=new Scene(root);
        Stage viewNewContact=new Stage();
        viewNewContact.setScene(scene);
        viewNewContact.show();
        
        stage=(Stage)paneCreateCustomer.getScene().getWindow();
        viewNewContact.setOnHiding(event -> {
                stage.show();
            });
        
        stage.close();
    }
    
    @FXML protected void onClicAddPhone(){
        Phone phone=new Phone();
        FXMLLoader loader=new FXMLLoader();
        Parent root=null;
        Scene scene;
        ViewNewPhoneController controller=null;
        
        loader.setLocation(getClass().getResource("viewNewPhone.fxml"));
        try {
            root=loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ViewCreateCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        controller=loader.getController();
        controller.initData(phone);
        customer.addPhone(phone);
        
        scene=new Scene(root);
        Stage viewNewPhone=new Stage();
        viewNewPhone.setScene(scene);
        viewNewPhone.show();
        
        stage=(Stage)paneCreateCustomer.getScene().getWindow();
        viewNewPhone.setOnHiding(event -> {
                stage.show();
            });
        
        stage.close();
    }
    
    @FXML protected void onClicAddScheme(){
        Scheme scheme=new Scheme();
        FXMLLoader loader=new FXMLLoader();
        Parent root=null;
        Scene scene;
        ViewNewSchemeController controller=null;
        
        loader.setLocation(getClass().getResource("viewNewScheme.fxml"));
        try {
            root=loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ViewCreateCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        controller=loader.getController();
        controller.initData(scheme);
        customer.addScheme(scheme);
        
        scene=new Scene(root);
        Stage viewNewScheme=new Stage();
        viewNewScheme.setScene(scene);
        viewNewScheme.show();
        
        stage=(Stage)paneCreateCustomer.getScene().getWindow();
        viewNewScheme.setOnHiding(event -> {
                stage.show();
            });
        
        stage.close();
    }
    
    @FXML protected void onClicBackFromAddress(){
        labelIntro.setText(introCompany);
        paneCompany.setVisible(true);
        paneAddress.setVisible(false);
        paneFootCompany.setVisible(true);
        paneFootAddress.setVisible(false);
    }
    
    @FXML protected void onClicBackFromFiscalData(){
        labelIntro.setText(introAddress);
        paneAddress.setVisible(true);
        paneFiscalData.setVisible(false);
        paneFootAddress.setVisible(true);
        paneFootFiscalData.setVisible(false);
    }
    
    @FXML protected void populateComboBox(){
        ObservableList<CustomProv> companyObs =FXCollections.observableArrayList(companies);
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
        CustomProv customProv=(CustomProv) cbCompany.getSelectionModel().getSelectedItem();
        
        fieldVAT.setText(customProv.getVatNumber());
        fieldComName.setText(customProv.getComName());
        fieldLegalName.setText(customProv.getLegalName());
        fieldEmailCompany.setText(customProv.getEmail());
        fieldWeb.setText(customProv.getWeb());
        fieldStreet.setText(customProv.getAddress().getStreet());
        fieldStNumber.setText(customProv.getAddress().getStNumber());
        fieldApt.setText(customProv.getAddress().getApt());
        fieldCP.setText(customProv.getAddress().getCp());
        fieldCity.setText(customProv.getAddress().getCity());
        fieldState.setText(customProv.getAddress().getState());
        fieldCountry.setText(customProv.getAddress().getCountry());
        
        customer.setIdCompany(customProv.getIdCompany());
        customer.getAddress().setIdAddress(customProv.getAddress().getIdAddress());   
    }
    
}

    

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invoicingapp_monolithic;
import com.invoicingapp.bbdd.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author frede
 */
public class InvoiceCustomer extends Document implements Comparable<InvoiceCustomer>{
    
    private double withholding;
    private boolean paid;
    private int idInvoiceCustomer;
    private Customer customer=new Customer();
    private LocalDate duedate;
    private int comparableValue;
    
    public InvoiceCustomer(){}
    
    public InvoiceCustomer(String docNumber, String title, double vat, LocalDate docDate, double retention, boolean paid){
        super(docNumber, title, vat, docDate);
        this.paid=paid;
        this.withholding=retention;
    }
    
    public InvoiceCustomer(String docNumber, String title, double vat, LocalDate docDate, double withholding){
        super(docNumber, title, vat, docDate);
        this.paid=false;
        this.withholding=withholding;
    }
    
    /**
    * Adds this invoiceCustomer instance to the Database.
    * Sets the idInvoiceCustomer from the new register in the DB
    */
    @Override
    public void addToDB(){
        ConnectionDB con=new ConnectionDB();
        String queryInsert;
        String queryGetId="SELECT MAX(idInvoiceCustomer) FROM InvoiceCustomer";
        ResultSet result=null;
        
        super.addToDB();
        
        queryInsert="INSERT INTO InvoiceCustomer (withholding, paid, idDocument) values("+withholding+","+paid+","+getIdDocument()+")";
        con.openConnection();
        con.noReturnQuery(queryInsert);
        result=con.getResultSet(queryGetId);
        try {
            result.next();
            idInvoiceCustomer=result.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(InvoiceCustomer.class.getName()).log(Level.SEVERE, null, ex);
        }
        con.closeConnection();
        
        setOrdersToBilled();
    }
    
    /**
    * Gets the invoiceCustomer from the Database and create an instance of that invoiceCustomer.
    * Gets the related document and customer instance from the DB.
    * @param id The idInvoiceCustomer of the invoiceCustomer to get from DB
    */
    @Override
    public void getFromDB (int id){
        ConnectionDB con=new ConnectionDB();
        String query="SELECT * FROM InvoiceCustomer WHERE idInvoiceCustomer="+id;
        
        String queryGetIdCustomer;
        ResultSet result=null;
        Orders order=new Orders();
        
        con.openConnection();
        result=con.getResultSet(query);
        
        try {
            if(result.next()){
                idInvoiceCustomer=result.getInt(1);
                withholding=result.getDouble(2);
                paid=result.getBoolean(3);
                super.getFromDB(result.getInt(4));
            }
        } catch (SQLException ex) {
            Logger.getLogger(InvoiceCustomer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        queryGetIdCustomer="SELECT Customer.idCustomer FROM Customer JOIN CustomProv ON (CustomProv.idCustomProv=Customer.idCustomProv) WHERE CustomProv.idCustomprov="+getOrders().get(1).getIdCustomProv();
        result=con.getResultSet(queryGetIdCustomer);
        try{
            if(result.next()){
                customer.getFromDB(result.getInt(1));
                duedate=getDocDate().plusDays(customer.getDuedate());
            }
        }catch (SQLException ex) {
            Logger.getLogger(InvoiceCustomer.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
        con.closeConnection();
    }
    
    /**
    * Cleans the table InvoiceCustomer in the DB.
    */
    public static void deleteAllFromDB(){
        ConnectionDB con=new ConnectionDB();
        String query="DELETE FROM InvoiceCustomer";
        
        con.openConnection();
        con.noReturnQuery(query);
        con.closeConnection();
    }
    
    /**
    * Deletes this invoiceCustomer instance from the DB. 
    */
    @Override
    public void deleteFromDB(){
        ConnectionDB con=new ConnectionDB();
        String query="DELETE FROM InvoiceCustomer WHERE idInvoiceCustomer="+idInvoiceCustomer;
                
        con.openConnection();
        con.noReturnQuery(query);
        con.closeConnection();
    }
    
    /**
    * Updates this invoiceCustomer in the DB.
    * @param field The field to update
    * @param newValue The new value for the field to update
    */
    @Override
    public void updateDB(String field, String newValue){
        ConnectionDB con=new ConnectionDB();
        String query="UPDATE InvoiceCustomer SET "+field+"= "+newValue+" WHERE idInvoiceCustomer="+idInvoiceCustomer;
        
        con.openConnection();
        con.noReturnQuery(query);
        con.closeConnection();
        getFromDB(idInvoiceCustomer);
    }
    
    @Override
    public void updateDB(String field, int newValue){
        ConnectionDB con=new ConnectionDB();
        String query="UDPATE InvoiceCustomer SET "+field+"= "+newValue+" WHERE idInvoiceCustomer="+idInvoiceCustomer;
        
        con.openConnection();
        con.noReturnQuery(query);
        con.closeConnection();
        getFromDB(idInvoiceCustomer);
    }
    
    public void updateDB(String field, double newValue){
        ConnectionDB con=new ConnectionDB();
        String query="UDPATE InvoiceCustomer SET "+field+"= "+newValue+" WHERE idInvoiceCustomer="+idInvoiceCustomer;
        
        con.openConnection();
        con.noReturnQuery(query);
        con.closeConnection();
        getFromDB(idInvoiceCustomer);
    }
    
    public void updateDB(String field, boolean newValue){
        ConnectionDB con=new ConnectionDB();
        String query="UDPATE InvoiceCustomer SET "+field+"= "+newValue+" WHERE idInvoiceCustomer="+idInvoiceCustomer;
        
        con.openConnection();
        con.noReturnQuery(query);
        con.closeConnection();
        getFromDB(idInvoiceCustomer);
    }
    
    /**
    * Gets the total of Withholding of this invoiceCustomer instance 
    * @return the total of Withholding for this invoiceCustomer instance
    */
    public double getTotalWithholding(){
        double totalWithholdingInvoiceCustomer=0;
        
        totalWithholdingInvoiceCustomer=getTotal()*withholding/100;
        
        return totalWithholdingInvoiceCustomer;
    }

    public double getTotalToPay(){
        double total=0;
        
        total=getTotal()+getTotalVAT()-getTotalWithholding();
        
        return total;
    }
    
    public void setComparableValueToDuedate(){
        comparableValue=duedate.getDayOfYear();
    }
    
    /**
     * @return the withholding
     */
    public double getWithholding() {
        return withholding;
    }

    /**
     * @param withholding the withholding to set
     */
    public void setWithholding(double withholding) {
        this.withholding = withholding;
    }

    /**
     * @return the paid
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * @param paid the paid to set
     */
    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    /**
     * @return the idInvoiceCustomer
     */
    public int getIdInvoiceCustomer() {
        return idInvoiceCustomer;
    }

    /**
     * @param idInvoiceCustomer the idInvoiceCustomer to set
     */
    public void setIdInvoiceCustomer(int idInvoiceCustomer) {
        this.idInvoiceCustomer = idInvoiceCustomer;
    }

    /**
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @param customer the idCustomer of the customer to set
     */
    public void setCustomer(Customer customer) {
        this.customer=customer;
    }

    /**
     * @return the duedate
     */
    public LocalDate getDuedate() {
        return duedate;
    }

    /**
     * @param duedate the duedate to set
     */
    public void setDuedate(LocalDate duedate) {
        this.duedate = duedate;
    }

    /**
     * @return the comparableValue
     */
    public int getComparableValue() {
        return comparableValue;
    }

    /**
     * @param comparableValue the comparableValue to set
     */
    public void setComparableValue(int comparableValue) {
        this.comparableValue = comparableValue;
    }
    
    @Override
    public int compareTo(InvoiceCustomer o) {
        return this.comparableValue-o.getComparableValue();
    }
}

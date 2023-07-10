/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Model;
import it.polito.tdp.yelp.model.Review;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnMiglioramento"
    private Button btnMiglioramento; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="cmbLocale"
    private ComboBox<Business> cmbLocale; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader
    
    @FXML
    void doRiempiLocali(ActionEvent event) {//azione invocata alla scelta di un valore dalla tendina citta'
    	this.cmbLocale.getItems().clear(); //pulire la tendina dei locali 
    	String citta = this.cmbCitta.getValue();
    	if(citta != null) {
    		List<Business> bus = model.businessDataCitta(citta);
    		this.cmbLocale.getItems().addAll(bus);    		
    	}
    	//se citta' e' null non si fa nulla, non si puo' invocare un errore, si potrebbe solo nel caso
    	//si prevedesse il valore stringa vuota nella tendina, ma non e' richiesto 
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	Business b = this.cmbLocale.getValue();
    	
    	//ogni volta che si crea un nuovo grafo non serve ripulire il resultField perche' 
    	//il primo comando e' un set text 
    	if(b != null) {
    		model.creaGrafo(b);
    		this.txtResult.setText(model.infoGrafo()); //set, cosi' se prima ci fosse stato l'avviso di errore si cancella, rimane piu' pulito
    		this.txtResult.appendText("\n\n");
    		this.txtResult.appendText(model.archiUscentiMax() +"\n");
    	}else {
    		this.txtResult.setText("Per favore scegliere un locale per poter creare il grafo.\n"
    				+ "(Per poter scegliere il locale è necessario selezionare una città");
    	}
    	
    }

    @FXML
    void doTrovaMiglioramento(ActionEvent event) {
    	
    	if(model.infoGrafo().compareTo("") == 0) {
    		this.txtResult.setText("Per favore, crere il grafo per poter calcolare \"miglioramento\"");
    		
    	}else {
    		this.txtResult.appendText(model.miglioramento());    		
    	}
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnMiglioramento != null : "fx:id=\"btnMiglioramento\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	List<String> citta = model.citiesOrdinate();
    	this.cmbCitta.getItems().addAll(citta); //i valori sono sempre questi, non serve pulire la tendina
    }
}

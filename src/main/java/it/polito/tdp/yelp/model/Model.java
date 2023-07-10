package it.polito.tdp.yelp.model;

import java.time.temporal.*;
import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	private YelpDao dao = new YelpDao();
	private SimpleDirectedWeightedGraph<Review, DefaultWeightedEdge> grafo;
	
	public List<String> citiesOrdinate(){		
		List<String> citta = dao.getCities();
		Collections.sort(citta); //meglio ordinare qui, ma si poteva fare anche nel dao
		return citta;
	}
	
	public List<Business> businessDataCitta(String c){
		List<Business> bus = dao.businessDataCitta(c);
		Collections.sort(bus); //ordinati secondo il nome
		return bus;
	}
	
	public void creaGrafo(Business b) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		//vertici
		String idLocale = b.getBusinessId();
		List<Review> vertici = dao.reviewsLocaleId(idLocale);
		Graphs.addAllVertices(grafo, vertici);
		
		//archi
		//per ogni recensione scorro le altre, se trovo che hanno data successiva faccio 
		//la differenza tra le due date e creo l'arco 
		//non facendo differenze in valori assoluti, non rischio di fare archi doppi
		for(Review r1: grafo.vertexSet()) {
			
			for(Review r2: grafo.vertexSet()) {
				
				if(!r1.equals(r2)) { //no loop
					
					if(r2.getDate().isAfter(r1.getDate())) {
						
						//se r2 e' successiva a r1 calcolo la differenza di giorni
						int delta = (int) ChronoUnit.DAYS.between(r1.getDate(), r2.getDate());
						
						//controllo se diverso da 0, ma in teoria non dovrebbe accadere
						//sarebbe zero solo se le due date fossero le stesse, ma isAfter
						//considera una data successiva ad un altra solo se effettivamente 
						//successiva, non e' un >= ma un > 
						
						if(delta > 0) {
							Graphs.addEdge(grafo, r1, r2, delta);
						}
					}
				}
			}			
		}
		
		System.out.println("Grafo creato: " + grafo.vertexSet().size() + " vertici," + grafo.edgeSet().size() + " archi");	
	}
	
	public String infoGrafo() {
		
		//se il grafo non è stato creato ritorna stringa vuota
		try {
			return "Grafo creato: " + grafo.vertexSet().size() + " vertici," + grafo.edgeSet().size() + " archi";
		}catch(NullPointerException npe) {
			return "";
		}
		
	}
	
	public String archiUscentiMax() {		
		String stampaRisultato = "";
		
		int max = 0;		
		List<String> id = new ArrayList<>();	
		
		for(Review r: grafo.vertexSet()) {			
			int numOut = grafo.outDegreeOf(r);
			
			if(numOut >= max) {
				
				if(numOut > max) {
					id = new ArrayList<>();
					id.add(r.getReviewId());					
				}
				if(numOut == max) {
					id.add(r.getReviewId());
				}
				max = numOut;
			
			}	
		}
		
		for(String s: id) {
			stampaRisultato+= s + "     #ARCHI USCENTI: " + max + "\n";
		}

		return stampaRisultato;	
	}
	
	private List<List<Review>> inizializzazioneRicorsione() {
		LinkedList<Review> parziale;
		List<List<Review>> sequenze = new LinkedList<>();		
		
		for(Review r: grafo.vertexSet()) {
			parziale = new LinkedList<>();
			parziale.add(r);
			ricorsione(parziale, sequenze);
		}
		
		
		return sequenze;
	}
	
	private void ricorsione(LinkedList<Review> par, List<List<Review>> seq) {
		
		//poiche' la ricorsione e' interna al model non passiamo il grafo come parametro, lo 
		//invochiamo direttamente

		double punteggioR = par.getLast().getStars();
		List<Review> successori = Graphs.successorListOf(grafo, par.getLast());
		for(Review rNext: successori) {
				if(rNext.getStars() >= punteggioR) {
					par.add(rNext);
					ricorsione(par, seq);
					par.removeLast();					
				}
		}
		//la ricorsione termina se arrivati ad un certo vertice non ho più successori da aggiungere
		seq.add(new LinkedList<>(par));		
	}
	
	
	
	
	
	
	public String miglioramento() {
		
		String s = "";
		List<List<Review>> soluzioni = this.inizializzazioneRicorsione();
		LinkedList<Review> migliore = new LinkedList<>();
		int max = 0;
		
		for(List<Review> lr: soluzioni) {
			int size = lr.size();
			if(size > max) {
				max = size;
				migliore = new LinkedList<>(lr);
			}
		}
		
		s+= "SEQUENZA ID DELLE RECENSIONI \"MIGLIORAMENTO\"\n";
		for(Review r: migliore) {
			s+= r.getReviewId()+"\n";
		}
		
		s+= "GIORNI TOTALI TRA LA PRIMA RECENSIONE E L'ULTIMA: "+
				ChronoUnit.DAYS.between(migliore.getFirst().getDate(), migliore.getLast().getDate());
		
		return s;
	}
	
	
}

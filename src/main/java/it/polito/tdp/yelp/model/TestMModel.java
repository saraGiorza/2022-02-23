package it.polito.tdp.yelp.model;

import java.util.List;

public class TestMModel {

	public static void main(String[] args) {
	
		Model m = new Model();
		
		List<Business> bus = m.businessDataCitta("Avondale");
		
		m.creaGrafo(bus.get(0));
		
		System.out.println(m.miglioramento());

	}

}

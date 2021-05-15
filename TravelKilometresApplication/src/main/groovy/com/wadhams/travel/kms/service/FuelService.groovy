package com.wadhams.travel.kms.service

import java.text.SimpleDateFormat

import com.wadhams.travel.kms.dto.FuelDTO

class FuelService {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
	
	List<FuelDTO> loadFuelData() {
		List<FuelDTO> fuelList = []
		
		File fuelFile
		URL resource = getClass().getClassLoader().getResource("Fuel.xml")
		if (resource == null) {
			throw new IllegalArgumentException("file not found!")
		} 
		else {
			fuelFile = new File(resource.toURI())
		}
		
		def fuel = new XmlSlurper().parse(fuelFile)
		def transactions = fuel.data

		transactions.each {txn ->
			//println txn
			fuelList << build(txn)
		}

		return fuelList
	}
	
	FuelDTO build(txn) {
			FuelDTO dto = new FuelDTO()
			
			//activityDate
			Date d = sdf.parse(txn.dt.text())
//			println d
			dto.fuelDate = d
			
			//odometer
			BigDecimal odometer = new BigDecimal(txn.odometer.text())
//			println odometer
			dto.odometer = odometer
			
			//dollarsPerLitre
			BigDecimal dollarsPerLitre = new BigDecimal(txn.dollarsPerLitre.text())
//			println dollarsPerLitre
			dto.dollarsPerLitre = dollarsPerLitre
			
			//litres
			BigDecimal litres = new BigDecimal(txn.litres.text())
//			println litres
			dto.litres = litres
			
			return dto
	}
}

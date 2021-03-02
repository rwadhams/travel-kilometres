package com.wadhams.travel.kms.service

import java.text.SimpleDateFormat

import com.wadhams.travel.kms.dto.TravelKilometerDTO
import com.wadhams.travel.kms.type.Activity

class FuelService {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
	
	List<TravelKilometerDTO> loadFuelData() {
		List<TravelKilometerDTO> tkList = []
		
		File fuelFile = new File('Fuel.xml')
		def fuel = new XmlSlurper().parse(fuelFile)
		def transactions = fuel.data

		TravelKilometerDTO dto = build(transactions[0])
		dto.activity = Activity.InitialFuel
		//println dto
		tkList << dto

		transactions[1..-1].each {txn ->
			//println txn
			dto = build(txn)
			dto.activity = Activity.FuelFillUp
			//println dto
			tkList << dto
		}

		return tkList
	}
	
	TravelKilometerDTO build(txn) {
			TravelKilometerDTO dto = new TravelKilometerDTO()
			
			//activityDate
			Date d = sdf.parse(txn.dt.text())
//			println d
			dto.activityDate = d
			
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

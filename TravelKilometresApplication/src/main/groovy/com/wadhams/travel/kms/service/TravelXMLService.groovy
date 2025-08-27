package com.wadhams.travel.kms.service

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.wadhams.travel.kms.dto.TravelDTO
import com.wadhams.travel.kms.type.Vehicle

class TravelXMLService {
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
	
	List<TravelDTO> loadTravelData() {
		List<TravelDTO> travelList = []
		
		File travelFile
		URL resource = getClass().getClassLoader().getResource("Travel.xml")
		if (resource == null) {
			throw new IllegalArgumentException("file not found!")
		} 
		else {
			travelFile = new File(resource.toURI())
		}
		
		def travel = new XmlSlurper().parse(travelFile)
		def transactions = travel.data

		transactions.each {txn ->
			//println txn
			travelList << build(txn)
		}

		return travelList
	}
	
	TravelDTO build(txn) {
			TravelDTO dto = new TravelDTO()
			
			//activityDate
			LocalDate ld = LocalDate.parse(txn.dt.text(), dtf)
//			println ld
			dto.travelDate = ld
			
			//vehicle
			String vehicle = txn.vehicle.text()
//			println vehicle
			dto.vehicle = Vehicle.findByXMLName(vehicle)
			
			//trailer
			String trailer = txn.trailer.text()
//			println trailer
			if (trailer) {
				dto.trailer = Vehicle.findByXMLName(trailer)
			}
			else {
				dto.trailer = Vehicle.NoTrailer
			}
			
			//departureLocation
			String departureLocation = txn.departureLocation.text()
//			println departureLocation
			dto.departureLocation = departureLocation
			
			//departureOdometer
			BigDecimal departureOdometer = new BigDecimal(txn.departureOdometer.text())
//			println departureOdometer
			dto.departureOdometer = departureOdometer
			
			//arrivalLocation
			String arrivalLocation = txn.arrivalLocation.text()
//			println arrivalLocation
			dto.arrivalLocation = arrivalLocation
			
			//arrivalOdometer
			BigDecimal arrivalOdometer = new BigDecimal(txn.arrivalOdometer.text())
//			println arrivalOdometer
			dto.arrivalOdometer = arrivalOdometer
			
			//arrivalCampsite
			String arrivalCampsite = txn.arrivalCampsite.text()
//			println arrivalCampsite
			dto.arrivalCampsite = arrivalCampsite
			
			return dto
	}
}

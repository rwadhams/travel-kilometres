package com.wadhams.travel.kms.service

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.wadhams.travel.kms.dto.CarOnlyDTO
import com.wadhams.travel.kms.dto.TravelDTO

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
			
			//departureLocation
			String departureLocation = txn.departureLocation.text()
//			println departureLocation
			dto.departureLocation = departureLocation
			
			//departureOdometer
			BigDecimal departureOdometer = new BigDecimal(txn.departureOdometer.text())
//			println departureOdometer
			dto.departureOdometer = departureOdometer
			
			//carOnly
			def carOnlys = txn.carOnly
			if (carOnlys) {
				carOnlys.each {co ->
					CarOnlyDTO coDTO = new CarOnlyDTO()
					coDTO.location = co.@location.text()
					String s1 = co.@dt.text()
					if (s1) {
						coDTO.date = LocalDate.parse(s1, dtf)
					}
					String s2 = co.@odometer.text()
					if (s2) {
						coDTO.odometer = new BigDecimal(s2)
					}
					dto.carOnlyList << coDTO
				}
			}
			
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

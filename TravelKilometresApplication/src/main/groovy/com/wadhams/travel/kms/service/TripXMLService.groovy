package com.wadhams.travel.kms.service

import java.text.SimpleDateFormat

import com.wadhams.travel.kms.dto.TripDTO

class TripXMLService {
	List<TripDTO> loadTripData() {
		List<TripDTO> tripList = []
		
		File tripFile
		URL resource = getClass().getClassLoader().getResource("Trip.xml")
		if (resource == null) {
			throw new IllegalArgumentException("file not found!")
		} 
		else {
			tripFile = new File(resource.toURI())
		}
		
		def trip = new XmlSlurper().parse(tripFile)
		def transactions = trip.data

		transactions.each {txn ->
			//println txn
			tripList << build(txn)
		}

		return tripList
	}
	
	TripDTO build(txn) {
			TripDTO dto = new TripDTO()
			
			//tripName
			String tripName = txn.name.text()
			//println tripName
			dto.tripName = tripName
			
			//start odometer
			BigDecimal startOdometer = new BigDecimal(txn.odometer.start.text())
			//println startOdometer
			dto.startOdometer = startOdometer
			
			//end odometer
			String s1 = txn.odometer.end.text()
			if (s1) {
				BigDecimal endOdometer = new BigDecimal(s1)
				//println endOdometer
				dto.endOdometer = endOdometer
			}
			else {
				dto.endOdometer = null
			}
			
			return dto
	}
}

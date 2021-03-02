package com.wadhams.travel.kms.service

import java.text.SimpleDateFormat

import com.wadhams.travel.kms.dto.TravelKilometerDTO
import com.wadhams.travel.kms.type.Activity

class TravelService {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
	
	List<TravelKilometerDTO> loadTravelData() {
		List<TravelKilometerDTO> tkList = []
		
		File travelFile = new File('Travel.xml')
		def travel = new XmlSlurper().parse(travelFile)
		def transactions = travel.data

		transactions.each {txn ->
			//println txn
			TravelKilometerDTO dto = build(txn)
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
			
			//activity
			String activity = txn.activity.text()
			//println activity
			switch (activity) {
				case 'DEPARTURE' :
					dto.activity = Activity.Departure
					break
				case 'ARRIVAL' :
					dto.activity = Activity.Arrival
					//campsite
					String campsite = txn.campsite.text()
					dto.campsite = campsite
					break
				default : 
					dto.activity = Activity.Unknown
			}
			
			//location
			String location = txn.location.text()
//			println location
			dto.location = location
			
			//odometer
			BigDecimal odometer = new BigDecimal(txn.odometer.text())
//			println odometer
			dto.odometer = odometer
			
			return dto
	}
}

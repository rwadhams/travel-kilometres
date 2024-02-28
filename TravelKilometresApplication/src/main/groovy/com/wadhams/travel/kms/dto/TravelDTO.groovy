package com.wadhams.travel.kms.dto

import groovy.transform.ToString

@ToString(includeNames=true)
class TravelDTO {
	Date travelDate
	
	String departureLocation
	BigDecimal departureOdometer
	
	List<CarOnlyDTO> carOnlyList = []
	
	String arrivalLocation
	BigDecimal arrivalOdometer
	String arrivalCampsite
}

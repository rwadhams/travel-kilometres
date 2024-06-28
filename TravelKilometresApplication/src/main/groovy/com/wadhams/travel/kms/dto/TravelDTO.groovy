package com.wadhams.travel.kms.dto

import java.time.LocalDate

import groovy.transform.ToString

@ToString(includeNames=true)
class TravelDTO {
	LocalDate travelDate
	
	String departureLocation
	BigDecimal departureOdometer
	
	List<CarOnlyDTO> carOnlyList = []
	
	String arrivalLocation
	BigDecimal arrivalOdometer
	String arrivalCampsite
}

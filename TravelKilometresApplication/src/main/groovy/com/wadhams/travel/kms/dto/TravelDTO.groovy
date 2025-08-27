package com.wadhams.travel.kms.dto

import java.time.LocalDate
import com.wadhams.travel.kms.type.Vehicle
import groovy.transform.ToString

@ToString(includeNames=true)
class TravelDTO {
	LocalDate travelDate
	
	Vehicle vehicle
	Vehicle trailer
	
	String departureLocation
	BigDecimal departureOdometer
	
	String arrivalLocation
	BigDecimal arrivalOdometer
	String arrivalCampsite
}

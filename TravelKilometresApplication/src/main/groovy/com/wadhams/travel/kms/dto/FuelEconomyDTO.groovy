package com.wadhams.travel.kms.dto

import groovy.transform.ToString

@ToString(includeNames=true)
class FuelEconomyDTO {
	TravelKilometerDTO fuelStart
	TravelKilometerDTO fuelEnd

	List<DepartureArrivalPair> dapList = []
	
	BigDecimal caravanKilometres = new BigDecimal(0)
	BigDecimal vehicleKilometres = new BigDecimal(0)
	
}

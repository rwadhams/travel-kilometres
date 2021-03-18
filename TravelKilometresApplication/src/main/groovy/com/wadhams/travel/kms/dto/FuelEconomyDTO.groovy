package com.wadhams.travel.kms.dto

import groovy.transform.ToString

@ToString(includeNames=true)
class FuelEconomyDTO {
	TravelKilometerDTO fuelStart
	TravelKilometerDTO fuelEnd

	List<TravelKilometerDTO> travelList = []
}

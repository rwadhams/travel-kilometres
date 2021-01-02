package com.wadhams.travel.kms.dto

import com.wadhams.travel.kms.type.Activity
import groovy.transform.ToString

@ToString(includeNames=true)
class TravelKilometerDTO {
	Activity activity
	
	Date activityDate
	BigDecimal odometer
	
	//Fuel
	BigDecimal dollarsPerLitre
	BigDecimal litres
	
}

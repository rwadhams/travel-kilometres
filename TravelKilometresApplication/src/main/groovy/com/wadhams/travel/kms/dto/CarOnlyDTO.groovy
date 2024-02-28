package com.wadhams.travel.kms.dto

import groovy.transform.ToString

@ToString(includeNames=true)
class CarOnlyDTO {
	//required
	String location
	
	//optional
	Date date
	BigDecimal odometer
}

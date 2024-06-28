package com.wadhams.travel.kms.dto

import java.time.LocalDate

import groovy.transform.ToString

@ToString(includeNames=true)
class CarOnlyDTO {
	//required
	String location
	
	//optional
	LocalDate date
	BigDecimal odometer
}

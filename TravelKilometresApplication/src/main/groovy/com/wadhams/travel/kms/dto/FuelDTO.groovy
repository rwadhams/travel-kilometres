package com.wadhams.travel.kms.dto

import groovy.transform.ToString

@ToString(includeNames=true)
class FuelDTO {
	Date fuelDate
	BigDecimal odometer
	BigDecimal dollarsPerLitre
	BigDecimal litres
}

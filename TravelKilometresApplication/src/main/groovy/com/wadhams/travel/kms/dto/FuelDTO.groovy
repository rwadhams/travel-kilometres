package com.wadhams.travel.kms.dto

import java.time.LocalDate

import groovy.transform.ToString

@ToString(includeNames=true)
class FuelDTO {
	LocalDate fuelDate
	BigDecimal odometer
	BigDecimal dollarsPerLitre
	BigDecimal litres
}

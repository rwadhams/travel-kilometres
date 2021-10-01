package com.wadhams.travel.kms.dto

import groovy.transform.ToString

@ToString(includeNames=true)
class TripDTO {
	String tripName
	BigDecimal startOdometer
	BigDecimal endOdometer
}

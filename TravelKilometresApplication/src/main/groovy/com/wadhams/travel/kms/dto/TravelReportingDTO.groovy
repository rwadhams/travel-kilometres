package com.wadhams.travel.kms.dto

import java.time.LocalDate
import com.wadhams.travel.kms.type.Vehicle
import groovy.transform.ToString

@ToString(includeNames=true)
class TravelReportingDTO {
	TravelDTO travelDTO
	
	BigDecimal locationToLocationKms
	BigDecimal kmsAroundLocation
}

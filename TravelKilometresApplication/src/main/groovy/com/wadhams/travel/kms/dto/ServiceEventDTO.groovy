package com.wadhams.travel.kms.dto

import groovy.transform.ToString
import java.time.LocalDate

@ToString(includeNames=true)
class ServiceEventDTO {
	LocalDate serviceEventDate
	String serviceEventLocation
	BigDecimal serviceEventCost
	BigDecimal serviceEventOdometer
	BigDecimal serviceEventScheduled
	String serviceEventName
}

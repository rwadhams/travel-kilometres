package com.wadhams.travel.kms.dto

import groovy.transform.ToString

@ToString(includeNames=true)
class ServiceEventDTO {
	Date serviceEventDate
	String serviceEventLocation
	BigDecimal serviceEventCost
	BigDecimal serviceEventSchedule
	String serviceEventName
	BigDecimal serviceEventOdometer
}

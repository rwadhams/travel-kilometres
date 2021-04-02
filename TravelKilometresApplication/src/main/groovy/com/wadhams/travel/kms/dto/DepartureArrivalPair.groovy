package com.wadhams.travel.kms.dto

import groovy.transform.ToString

@ToString(includeNames=true)
class DepartureArrivalPair {
	TravelKilometerDTO departure
	TravelKilometerDTO arrival
}

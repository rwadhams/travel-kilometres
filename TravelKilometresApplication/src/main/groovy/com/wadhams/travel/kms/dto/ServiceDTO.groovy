package com.wadhams.travel.kms.dto

import groovy.transform.ToString

@ToString(includeNames=true)
class ServiceDTO {
	BigDecimal transmissionFrequency
	BigDecimal caravanFrequency
	BigDecimal carFrequency
	
	List<ServiceEventDTO> transmissionList = []
	List<ServiceEventDTO> caravanList = []
	List<ServiceEventDTO> carList = []
}

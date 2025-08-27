package com.wadhams.travel.kms.dto

import com.wadhams.travel.kms.type.Reporting
import com.wadhams.travel.kms.type.ServiceTiming
import com.wadhams.travel.kms.type.Vehicle
import groovy.transform.ToString

@ToString(includeNames=true)
class ServiceDTO {
	String name
	BigDecimal frequency
	Vehicle vehicle				//See Vehicle Enum
	Reporting reporting			//Service or Consumable
	ServiceTiming serviceTiming	//Scheduled or Unscheduled.
	
	List<ServiceEventDTO> serviceEventDTOList
}

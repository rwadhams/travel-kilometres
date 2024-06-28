package com.wadhams.travel.kms.service

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.wadhams.travel.kms.dto.ServiceContainerDTO
import com.wadhams.travel.kms.dto.ServiceDTO
import com.wadhams.travel.kms.dto.ServiceEventDTO
import com.wadhams.travel.kms.type.Reporting
import com.wadhams.travel.kms.type.Vehicle
import com.wadhams.travel.kms.type.ServiceTiming

class ServiceXMLService {
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
	
	ServiceContainerDTO loadServiceData() {
		ServiceContainerDTO sc = new ServiceContainerDTO()
		
		File serviceFile
		URL resource = getClass().getClassLoader().getResource("Service.xml")
		if (resource == null) {
			throw new IllegalArgumentException("file not found!")
		} 
		else {
			serviceFile = new File(resource.toURI())
		}
		
		def txn = new XmlSlurper().parse(serviceFile)
		
		def services = txn.service
		services.each {sXML ->
			ServiceDTO s = buildServiceDTO(sXML)
			s.reporting = Reporting.Service
			if (sXML.serviceTiming.text() == 'SCHEDULED') {
				s.serviceTiming = ServiceTiming.Scheduled
			}
			else {
				s.serviceTiming = ServiceTiming.UnScheduled
			}
			s.serviceEventDTOList = buildServiceEventDTOList(sXML.event) 
			sc.serviceDTOList << s
		}
		
		def consumables = txn.consumable
		consumables.each {cXML ->
			ServiceDTO s = buildServiceDTO(cXML)
			s.reporting = Reporting.Consumable
			s.serviceEventDTOList = buildServiceEventDTOList(cXML.event) 
			sc.serviceDTOList << s
		}
		
		return sc
	}
	
	ServiceDTO buildServiceDTO(txn) {
		ServiceDTO s = new ServiceDTO()
		//name
		s.name = txn.name.text()
		
		//frequency
		String frequency = txn.frequency.text()
		if (frequency) {
			s.frequency = new BigDecimal(frequency)
		}
		
		//vehicle
		String vehicle = txn.vehicle.text()
		if (vehicle.toUpperCase() == 'CAR') {
			s.vehicle = Vehicle.Car
		}
		else if (vehicle.toUpperCase() == 'CARAVAN') {
			s.vehicle = Vehicle.Caravan
		}

		return s
	}
	
	List<ServiceEventDTO> buildServiceEventDTOList(events) {
		List<ServiceEventDTO> serviceEventDTOList = []
		
		events.each {e ->
			ServiceEventDTO se = new ServiceEventDTO()
			
			//serviceEventDate
			LocalDate ld = LocalDate.parse(e.dt.text(), dtf)
			//println d
			se.serviceEventDate = ld
			
			//serviceEventLocation
			String serviceEventLocation = e.location.text()
			//println serviceEventLocation
			se.serviceEventLocation = serviceEventLocation
			
			//serviceEventCost
			String cost = e.cost.text()
			if (cost) {
				BigDecimal serviceEventCost = new BigDecimal(cost)
				//println serviceEventCost
				se.serviceEventCost = serviceEventCost
			}
			
			//serviceEventOdometer
			String odometer = e.odometer.text()
			if (odometer) {
				BigDecimal serviceEventOdometer = new BigDecimal(odometer)
				//println serviceEventOdometer
				se.serviceEventOdometer = serviceEventOdometer
			}

			//serviceEventScheduled
			String scheduled = e.scheduled.text()
			if (scheduled) {
				BigDecimal serviceEventScheduled = new BigDecimal(scheduled)
				//println serviceEventSchedule
				se.serviceEventScheduled = serviceEventScheduled
			}
			
			//serviceEventName
			String eventName = e.serviceName.text()
			if (eventName) {
				se.serviceEventName = eventName
			}
			
			serviceEventDTOList << se
		}
		
		return serviceEventDTOList
	}
}

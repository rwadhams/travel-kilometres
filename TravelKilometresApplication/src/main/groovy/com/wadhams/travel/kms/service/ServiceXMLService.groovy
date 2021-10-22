package com.wadhams.travel.kms.service

import java.text.SimpleDateFormat

import com.wadhams.travel.kms.dto.ServiceDTO
import com.wadhams.travel.kms.dto.ServiceEventDTO

class ServiceXMLService {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
	
	ServiceDTO loadServiceData() {
		ServiceDTO dto = new ServiceDTO()
		
		File serviceFile
		URL resource = getClass().getClassLoader().getResource("Service.xml")
		if (resource == null) {
			throw new IllegalArgumentException("file not found!")
		} 
		else {
			serviceFile = new File(resource.toURI())
		}
		
		def service = new XmlSlurper().parse(serviceFile)
		
		//transmission
		def transmission = service.transmission
		dto.transmissionFrequency = new BigDecimal(transmission.frequency.text())
		transmission.data.each {txn ->
			//println txn
			dto.transmissionList << build(txn)
		}

		//car tyres
		def carTyres = service.carTyres
		carTyres.data.each {txn ->
			//println txn
			dto.carTyresList << build(txn)
		}

		//fuel filter
		def fuelFilter = service.fuelFilter
		fuelFilter.data.each {txn ->
			//println txn
			dto.fuelFilterList << build(txn)
		}

		//caravan
		def caravan = service.caravan
		dto.caravanFrequency = new BigDecimal(caravan.frequency.text())
		caravan.data.each {txn ->
			//println txn
			dto.caravanList << build(txn)
		}

		//car
		def car = service.car
		dto.carFrequency = new BigDecimal(car.frequency.text())
		car.data.each {txn ->
			//println txn
			dto.carList << build(txn)
		}

		return dto
	}
	
	ServiceEventDTO build(txn) {
		ServiceEventDTO dto = new ServiceEventDTO()
			
		//serviceEventDate
		Date d = sdf.parse(txn.dt.text())
		//println d
		dto.serviceEventDate = d
		
		//serviceEventLocation
		String serviceEventLocation = txn.location.text()
		//println serviceEventLocation
		dto.serviceEventLocation = serviceEventLocation
		
		//serviceEventCost
		BigDecimal serviceEventCost = new BigDecimal(txn.cost.text())
		//println serviceEventCost
		dto.serviceEventCost = serviceEventCost
		
		//serviceEventSchedule
		String s1 = txn.schedule.text()
		if (s1) {
			BigDecimal serviceEventSchedule = new BigDecimal(s1)
			//println serviceEventSchedule
			dto.serviceEventSchedule = serviceEventSchedule
		}
		
		//serviceEventOdometer
		BigDecimal serviceEventOdometer = new BigDecimal(txn.odometer.text())
		//println serviceEventOdometer
		dto.serviceEventOdometer = serviceEventOdometer

		return dto
	}
}

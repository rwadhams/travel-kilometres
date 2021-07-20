package com.wadhams.travel.kms.service

import com.wadhams.travel.kms.dto.TravelDTO

class TravelListService {
	BigDecimal totalCaravanKms(List<TravelDTO> travelList) {
		BigDecimal totalCaravanKms = new BigDecimal(0.0)
		
		travelList.each {t ->
			BigDecimal caravanKms = t.arrivalOdometer.subtract(t.departureOdometer)
			totalCaravanKms = totalCaravanKms.add(caravanKms)
		}
		
		return totalCaravanKms
	}
	
	BigDecimal carOdometerKms(List<TravelDTO> travelList) {
		return travelList[-1].arrivalOdometer
	}
}

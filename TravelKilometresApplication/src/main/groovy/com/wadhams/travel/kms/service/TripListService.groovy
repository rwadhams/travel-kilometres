package com.wadhams.travel.kms.service

import com.wadhams.travel.kms.dto.TravelDTO
import com.wadhams.travel.kms.dto.TripDTO

class TripListService {
	def fixMissingEndOdometer(List<TripDTO> tripList, List<TravelDTO> travelList) {
		TripDTO tripDTO = tripList[-1]	//last entry
		if (tripDTO.endOdometer == null) {
			TravelDTO travelDTO = travelList[-1]	//last entry
			tripDTO.endOdometer = new BigDecimal(travelDTO.arrivalOdometer)
		}
	}
}

package com.wadhams.travel.kms.controller

import com.wadhams.travel.kms.dto.FuelDTO
import com.wadhams.travel.kms.dto.TravelDTO
import com.wadhams.travel.kms.dto.TripDTO
import com.wadhams.travel.kms.report.TripReportService
import com.wadhams.travel.kms.service.FuelXMLService
import com.wadhams.travel.kms.service.TravelXMLService
import com.wadhams.travel.kms.service.TripListService
import com.wadhams.travel.kms.service.TripXMLService

class TempController {
	def execute() {
		FuelXMLService fuelXMLService = new FuelXMLService()
		List<FuelDTO> fuelList = fuelXMLService.loadFuelData()

		TravelXMLService travelXMLService = new TravelXMLService()
		List<TravelDTO> travelList = travelXMLService.loadTravelData()

		TripXMLService tripXMLService = new TripXMLService()
		List<TripDTO> tripList = tripXMLService.loadTripData()
//		tripList.each {trip ->
//			println trip
//		}

		TripListService tripListService = new TripListService()
		tripListService.fixMissingEndOdometer(tripList, travelList)
//		tripList.each {trip ->
//			println trip
//		}

		TripReportService service = new TripReportService()
		service.execute(tripList, fuelList, travelList)

	}
}


package com.wadhams.travel.kms.controller

import com.wadhams.travel.kms.dto.TravelKilometerDTO
import com.wadhams.travel.kms.report.FuelDetailReportService
import com.wadhams.travel.kms.report.TravelReportService
import com.wadhams.travel.kms.service.FuelService
import com.wadhams.travel.kms.service.TravelService

class TravelKilometresController {
	
	def execute() {
		FuelService fuelService = new FuelService()
		List<TravelKilometerDTO> fuelList = fuelService.loadFuelData()
		
		FuelDetailReportService fuelDetail = new FuelDetailReportService()
		fuelDetail.execute(fuelList)
		
		TravelService travelService = new TravelService()
		List<TravelKilometerDTO> travelList = travelService.loadTravelData()
		
		TravelReportService travel = new TravelReportService()
		travel.execute(travelList)
		
	}
}

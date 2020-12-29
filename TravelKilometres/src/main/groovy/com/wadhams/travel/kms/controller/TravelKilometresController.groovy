package com.wadhams.travel.kms.controller

import com.wadhams.travel.kms.dto.TravelKilometerDTO
import com.wadhams.travel.kms.report.FuelDetailReportService
import com.wadhams.travel.kms.service.FuelService

class TravelKilometresController {
	
	def execute() {
		FuelService fuelService = new FuelService()
		List<TravelKilometerDTO> tkList = fuelService.loadFuelData()
		
		FuelDetailReportService fuelDetail = new FuelDetailReportService()
		fuelDetail.execute(tkList)
		
		
	}
//		CategoryDetailReportService categoryDetailReportService = new CategoryDetailReportService()
//		categoryDetailReportService.execute()
}

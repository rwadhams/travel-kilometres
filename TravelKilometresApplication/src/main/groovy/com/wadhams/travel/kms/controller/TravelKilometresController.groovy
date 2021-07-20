package com.wadhams.travel.kms.controller

import java.math.BigDecimal

import com.wadhams.travel.kms.dto.FuelDTO
import com.wadhams.travel.kms.dto.ServiceDTO
import com.wadhams.travel.kms.dto.TravelDTO
import com.wadhams.travel.kms.report.FuelDetailReportService
import com.wadhams.travel.kms.report.ServiceReportService
import com.wadhams.travel.kms.report.TravelReportService
import com.wadhams.travel.kms.service.TravelListService
import com.wadhams.travel.kms.service.FuelXMLService
import com.wadhams.travel.kms.service.ServiceXMLService
import com.wadhams.travel.kms.service.TravelXMLService

class TravelKilometresController {
	
	def execute() {
		FuelXMLService fuelXMLService = new FuelXMLService()
		List<FuelDTO> fuelList = fuelXMLService.loadFuelData()
		
		FuelDetailReportService fuelDetail = new FuelDetailReportService()
		fuelDetail.execute(fuelList)
		
		TravelXMLService travelXMLService = new TravelXMLService()
		List<TravelDTO> travelList = travelXMLService.loadTravelData()
		
		TravelReportService travel = new TravelReportService()
		travel.execute(travelList)
		
		TravelListService travelListService = new TravelListService()
		BigDecimal totalCaravanKms = travelListService.totalCaravanKms(travelList)
		BigDecimal carOdometerKms = travelListService.carOdometerKms(travelList)
		
		ServiceXMLService serviceXMLService = new ServiceXMLService()
		ServiceDTO serviceDTO = serviceXMLService.loadServiceData()
		
		ServiceReportService service = new ServiceReportService()
		service.execute(serviceDTO, totalCaravanKms, carOdometerKms)
	}
}

package com.wadhams.travel.kms.controller

import com.wadhams.travel.kms.comparator.FuelEconomyPerformanceComparator
import com.wadhams.travel.kms.dto.FuelDTO
import com.wadhams.travel.kms.dto.FuelEconomyDTO
import com.wadhams.travel.kms.dto.TravelDTO
import com.wadhams.travel.kms.report.FuelDetailReportService
import com.wadhams.travel.kms.report.FuelEconomyReportService
import com.wadhams.travel.kms.report.TravelReportService
import com.wadhams.travel.kms.service.FuelEconomyService
import com.wadhams.travel.kms.service.FuelXMLService
import com.wadhams.travel.kms.service.TravelXMLService

class FuelEconomyController {
	
	def execute() {
		FuelXMLService fuelXMLService = new FuelXMLService()
		List<FuelDTO> fuelList = fuelXMLService.loadFuelData()
		
		FuelEconomyService feService = new FuelEconomyService()
		List<FuelEconomyDTO> feList = feService.buildFuelEconomyList(fuelList)
//		feList.each {fe ->
//			println fe
//		}
//		println ''
		
		TravelXMLService travelXMLService = new TravelXMLService()
		List<TravelDTO> travelList = travelXMLService.loadTravelData()
		
		feService.addCaravanTripsToFuelEconomyList(feList, travelList)
		
		feService.calculateAdditionalValues(feList)
		
		FuelEconomyReportService ferService = new FuelEconomyReportService()
		
		ferService.reportByDate(feList)
		
		ferService.reportByPerformance(feList)
		
	}
}

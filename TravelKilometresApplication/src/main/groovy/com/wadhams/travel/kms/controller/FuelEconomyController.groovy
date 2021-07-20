package com.wadhams.travel.kms.controller

import com.wadhams.travel.kms.dto.FuelDTO
import com.wadhams.travel.kms.dto.FuelEconomyDTO
import com.wadhams.travel.kms.dto.TravelDTO
import com.wadhams.travel.kms.report.FuelDetailReportService
import com.wadhams.travel.kms.report.TravelReportService
import com.wadhams.travel.kms.service.FuelEconomyService
import com.wadhams.travel.kms.service.FuelXMLService
import com.wadhams.travel.kms.service.TravelXMLService

class FuelEconomyController {
	
	def execute() {
		FuelXMLService fuelXMLService = new FuelXMLService()
		List<FuelDTO> fuelList = fuelXMLService.loadFuelData()
		
//		fuelList.each {f ->
//			println f
//		}
//		println ''
		
		FuelEconomyService feService = new FuelEconomyService()
		List<FuelEconomyDTO> feList = feService.buildFuelEconomyList(fuelList, '29/09/2019')	//include, if after this date
//		List<FuelEconomyDTO> feList = feService.buildFuelEconomyList(fuelList, '30/06/2020')	//include, if after this date
//		List<FuelEconomyDTO> feList = feService.buildFuelEconomyList(fuelList, '25/12/2020')	//include, if after this date
//		List<FuelEconomyDTO> feList = feService.buildFuelEconomyList(fuelList, '01/01/2021')	//include, if after this date
//		feList.each {fe ->
//			println fe
//		}
//		println ''
		
		TravelXMLService travelXMLService = new TravelXMLService()
		List<TravelDTO> travelList = travelXMLService.loadTravelData()
		
		feService.addCaravanTripsFuelEconomyList(feList, travelList)
		
		feService.calculateCaravanVehicleKilometres(feList)
		
//		feList.each {fe ->
//			println fe
//		}
//		println ''

		List<String> reportList = feService.buildReport(feList)
		reportList.each {r ->
			println r
		}

		Map<BigDecimal, List<BigDecimal>> map = feService.buildPivotData(feList)
		List<String> pivotList = feService.buildPivotReport(map)
		pivotList.each {r ->
			println r
		}
	}
}

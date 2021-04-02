package com.wadhams.travel.kms.controller

import com.wadhams.travel.kms.dto.FuelEconomyDTO
import com.wadhams.travel.kms.dto.TravelKilometerDTO
import com.wadhams.travel.kms.report.FuelDetailReportService
import com.wadhams.travel.kms.report.TravelReportService
import com.wadhams.travel.kms.service.FuelEconomyService
import com.wadhams.travel.kms.service.FuelService
import com.wadhams.travel.kms.service.TravelService
import com.wadhams.travel.kms.dto.DepartureArrivalPair

class FuelEconomyController {
	
	def execute() {
		FuelService fuelService = new FuelService()
		List<TravelKilometerDTO> fuelList = fuelService.loadFuelData()
		
//		fuelList.each {tk ->
//			println tk
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
		
		TravelService travelService = new TravelService()
		List<TravelKilometerDTO> travelList = travelService.loadTravelData()
		
		List<DepartureArrivalPair> dapList = feService.buildDepartureArrivalPairList(travelList)
		
//		println "dapList size(): ${dapList.size()}"
//		println ''
		
		feService.addCaravanTripsFuelEconomyList(feList, dapList)
		
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

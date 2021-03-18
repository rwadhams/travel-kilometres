package com.wadhams.travel.kms.controller

import com.wadhams.travel.kms.dto.FuelEconomyDTO
import com.wadhams.travel.kms.dto.TravelKilometerDTO
import com.wadhams.travel.kms.report.FuelDetailReportService
import com.wadhams.travel.kms.report.TravelReportService
import com.wadhams.travel.kms.service.FuelEconomyService
import com.wadhams.travel.kms.service.FuelService
import com.wadhams.travel.kms.service.TravelService

class FuelEconomyController {
	
	def execute() {
		FuelService fuelService = new FuelService()
		List<TravelKilometerDTO> fuelList = fuelService.loadFuelData()
		
//		fuelList.each {tk ->
//			println tk
//		}
//		println ''
		
		FuelEconomyService feService = new FuelEconomyService()
		List<FuelEconomyDTO> feList = feService.build(fuelList, '01/01/2021')	//include, if after this date
//		feList.each {fe ->
//			//println fe
//			println "Date: ${fe.fuelStart.activityDate}\tKms: ${fe.fuelEnd.odometer.subtract(fe.fuelStart.odometer)}"
//		}
		
		TravelService travelService = new TravelService()
		List<TravelKilometerDTO> travelList = travelService.loadTravelData()
		
		feService.augmentFuelEconomyList(feList, travelList)
		println ''
		feList.each {fe ->
			println "Date: ${fe.fuelStart.activityDate}\tKms: ${fe.fuelEnd.odometer.subtract(fe.fuelStart.odometer)}\tTravels: ${fe.travelList.size()}"
			println "Start: ${fe.fuelStart.odometer}\tEnd: ${fe.fuelEnd.odometer}"
			fe.travelList.each {t ->
				println "\t${t.odometer}"
			}
		}

		
//		FuelDetailReportService fuelDetail = new FuelDetailReportService()
//		fuelDetail.execute(fuelList)
//		
//		TravelService travelService = new TravelService()
//		List<TravelKilometerDTO> travelList = travelService.loadTravelData()
//		
//		TravelReportService travel = new TravelReportService()
//		travel.execute(travelList)
		
	}
}

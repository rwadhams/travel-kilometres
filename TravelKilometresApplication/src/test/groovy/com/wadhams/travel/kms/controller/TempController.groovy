package com.wadhams.travel.kms.controller

import com.wadhams.travel.kms.biz.OdometerMap
import com.wadhams.travel.kms.dto.FuelDTO
import com.wadhams.travel.kms.dto.FuelEconomyDTO
import com.wadhams.travel.kms.dto.ServiceDTO
import com.wadhams.travel.kms.dto.TravelDTO
import com.wadhams.travel.kms.dto.TripDTO
import com.wadhams.travel.kms.report.FuelDetailReportService
import com.wadhams.travel.kms.report.FuelEconomyReportService
import com.wadhams.travel.kms.report.ServiceReportService
import com.wadhams.travel.kms.report.TravelReportService
import com.wadhams.travel.kms.report.TripReportService
import com.wadhams.travel.kms.service.FuelEconomyService
import com.wadhams.travel.kms.service.FuelXMLService
import com.wadhams.travel.kms.service.OdometerService
import com.wadhams.travel.kms.service.ServiceXMLService
import com.wadhams.travel.kms.service.TravelXMLService
import com.wadhams.travel.kms.service.TripXMLService

class TempController {
	def execute() {
		FuelXMLService fuelXMLService = new FuelXMLService()
		List<FuelDTO> fuelList = fuelXMLService.loadFuelData()
//		fuelList.each {f ->
//			println f
//		}

		TravelXMLService travelXMLService = new TravelXMLService()
		List<TravelDTO> travelList = travelXMLService.loadTravelData()
//		travelList.each {t ->
//			println t
//		}
		
		ServiceXMLService serviceXMLService = new ServiceXMLService()
		List<ServiceDTO> serviceList = serviceXMLService.loadServiceData()
//		serviceList.each {s ->
//			println s
//		}
		
		TripXMLService tripXMLService = new TripXMLService()
		List<TripDTO> tripList = tripXMLService.loadTripData(travelList)
//		tripList.each {trip ->
//			println trip
//		}

		OdometerService odometerService = new OdometerService()
		OdometerMap odometerMap = odometerService.buildOdometers(travelList)
//		println 'Vehicle Odometer:'
//		println "ToyotaLandCruiser...: ${odometerMap.getOdometer(Vehicle.ToyotaLandCruiser)}"
//		println "SaluteCaravan.......: ${odometerMap.getOdometer(Vehicle.SaluteCaravan)}"
//		println "KimberleyKamper.....: ${odometerMap.getOdometer(Vehicle.KimberleyKamper)}"
		
		FuelEconomyService fuelEconomyService = new FuelEconomyService()
		List<FuelEconomyDTO> feList = fuelEconomyService.buildFuelEconomyList(fuelList)
		fuelEconomyService.addTrailerTripsToFuelEconomyList(feList, travelList)
		fuelEconomyService.calculateAdditionalValues(feList)
//		feList.each {fe ->
//			println fe
//			println '--------------------------------'
//		}

		//REPORTS
		
		FuelDetailReportService fuelDetailReportService = new FuelDetailReportService()
		fuelDetailReportService.execute(fuelList)
		
		TravelReportService travelReportService = new TravelReportService()
		travelReportService.execute(travelList)

		ServiceReportService serviceReportService = new ServiceReportService()
		serviceReportService.execute(serviceList, odometerMap)

		FuelEconomyReportService fuelEconomyReportService = new FuelEconomyReportService()
		fuelEconomyReportService.reportByDate(feList)
		fuelEconomyReportService.reportByPerformance(feList)
		
		TripReportService tripReportService = new TripReportService()
		tripReportService.execute(tripList, travelList)

	}
}


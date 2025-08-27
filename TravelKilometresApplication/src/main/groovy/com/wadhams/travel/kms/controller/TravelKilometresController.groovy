package com.wadhams.travel.kms.controller

import com.wadhams.travel.kms.biz.OdometerContainer
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

class TravelKilometresController {
	
	def execute() {
		FuelXMLService fuelXMLService = new FuelXMLService()
		List<FuelDTO> fuelList = fuelXMLService.loadFuelData()

		TravelXMLService travelXMLService = new TravelXMLService()
		List<TravelDTO> travelList = travelXMLService.loadTravelData()
		
		ServiceXMLService serviceXMLService = new ServiceXMLService()
		List<ServiceDTO> serviceList = serviceXMLService.loadServiceData()
		
		TripXMLService tripXMLService = new TripXMLService()
		List<TripDTO> tripList = tripXMLService.loadTripData(travelList)

		OdometerService odometerService = new OdometerService()
		OdometerContainer odometerContainer = odometerService.buildOdometers(travelList)
		
		FuelEconomyService fuelEconomyService = new FuelEconomyService()
		List<FuelEconomyDTO> feList = fuelEconomyService.buildFuelEconomyList(fuelList)
		fuelEconomyService.addTrailerTripsToFuelEconomyList(feList, travelList)
		fuelEconomyService.calculateAdditionalValues(feList)

		//REPORTS
		
		FuelDetailReportService fuelDetailReportService = new FuelDetailReportService()
		fuelDetailReportService.execute(fuelList)
		
		TravelReportService travelReportService = new TravelReportService()
		travelReportService.execute(travelList)

		ServiceReportService serviceReportService = new ServiceReportService()
		serviceReportService.execute(serviceList, odometerContainer)

		FuelEconomyReportService fuelEconomyReportService = new FuelEconomyReportService()
		fuelEconomyReportService.reportByDate(feList)
		fuelEconomyReportService.reportByPerformance(feList)
		
		TripReportService tripReportService = new TripReportService()
		tripReportService.execute(tripList, travelList)
	}
}

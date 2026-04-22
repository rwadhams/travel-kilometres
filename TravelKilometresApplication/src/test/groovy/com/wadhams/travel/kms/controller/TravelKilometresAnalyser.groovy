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
import com.wadhams.travel.kms.type.Vehicle

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import com.wadhams.travel.kms.dto.TravelReportingDTO


class TravelKilometresAnalyser {
	
	def execute() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)

		TravelXMLService travelXMLService = new TravelXMLService()
		List<TravelDTO> travelList = travelXMLService.loadTravelData()
		println "travelList size.....: ${travelList.size()}"
		println "travelList[first]...: ${travelList[0]}"
		println "travelList[last]....: ${travelList[-1]}"
		println ''
//		travelList.each {t ->
//			println t
//		}

		int maxArrivalLocationSize = maxArrivalLocationSize(travelList)
//		println "maxArrivalLocationSize.....: $maxArrivalLocationSize"
//		println ''

		OdometerService odometerService = new OdometerService()
		OdometerMap odometerMap = odometerService.buildOdometers(travelList)
		println 'Vehicle Odometer:'
		println "ToyotaLandCruiser...: ${odometerMap.getOdometer(Vehicle.ToyotaLandCruiser)}"
		println "SaluteCaravan.......: ${odometerMap.getOdometer(Vehicle.SaluteCaravan)}"
		println "KimberleyKamper.....: ${odometerMap.getOdometer(Vehicle.KimberleyKamper)}"
		println ''
		
		println "Starting location...: ${travelList[0].departureLocation}"
		println "Starting odometer...: ${nf.format(travelList[0].departureOdometer)}"
		println ''
		println '1st number: locationToLocationKms'
		println '2nd number: kmsAroundLocation without a trailer'
		println ''
		
		BigDecimal toyotaLandCruiserKms = BigDecimal.ZERO
		BigDecimal saluteCaravanKms = BigDecimal.ZERO
		BigDecimal kimberleyKamperKms = BigDecimal.ZERO
		
		TravelReportService travelReportService = new TravelReportService()
		List<TravelReportingDTO> travelReportingDTOList = travelReportService.buildTravelReportingDTOList(travelList)
		travelReportingDTOList.each {tr ->
			TravelDTO t = tr.travelDTO
			String s1 = t.travelDate.format(dtf)
			String s2 = t.arrivalLocation.padRight(maxArrivalLocationSize, ' ')
			
			String s6 = nf.format(tr.locationToLocationKms).padRight(6, ' ')
			String s7 = nf.format(tr.kmsAroundLocation).padRight(6, ' ')
			String s8 = t.trailer
			
			println "$s1 $s2 $s6 $s7 $s8"
			
			toyotaLandCruiserKms = toyotaLandCruiserKms.add(tr.locationToLocationKms).add(tr.kmsAroundLocation)
			if (t.trailer == Vehicle.SaluteCaravan) {
				saluteCaravanKms = saluteCaravanKms.add(tr.locationToLocationKms)
			}
			if (t.trailer == Vehicle.KimberleyKamper) {
				kimberleyKamperKms = kimberleyKamperKms.add(tr.locationToLocationKms)
			}
		}
		println ''
		println "ToyotaLandCruiserKms....: ${nf.format(toyotaLandCruiserKms)}"
		println "SaluteCaravanKms........: ${nf.format(saluteCaravanKms)}"
		println "KimberleyKamperKms......: ${nf.format(kimberleyKamperKms)}"
		
		
/*				
		FuelXMLService fuelXMLService = new FuelXMLService()
		List<FuelDTO> fuelList = fuelXMLService.loadFuelData()
//		fuelList.each {f ->
//			println f
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
*/
	}
	
	int maxArrivalLocationSize(List<TravelDTO> travelList) {
		int maxLocationSize = 0
		travelList.each {t ->
			if (t.arrivalLocation.size() > maxLocationSize) {
				maxLocationSize = t.arrivalLocation.size()
			}
		}
		return maxLocationSize
	}
}


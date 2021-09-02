package com.wadhams.travel.kms.service

import java.math.MathContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import com.wadhams.travel.kms.comparator.FuelEconomyDateComparator
import com.wadhams.travel.kms.comparator.FuelEconomyPerformanceComparator
import com.wadhams.travel.kms.dto.FuelDTO
import com.wadhams.travel.kms.dto.FuelEconomyDTO
import com.wadhams.travel.kms.dto.TravelDTO

class FuelEconomyService {
	List<FuelEconomyDTO> buildFuelEconomyList(List<FuelDTO> fuelList) {
		List<FuelEconomyDTO> feList = []
		
		assert fuelList.size() >= 2
		
		for (int i; i < fuelList.size()-1; i++) {
			FuelEconomyDTO dto = new FuelEconomyDTO()
			
			dto.fuelStart = fuelList[i]
			dto.fuelEnd = fuelList[i+1]
			
			feList << dto
		}

		return feList
	}
	
	def addCaravanTripsToFuelEconomyList(List<FuelEconomyDTO> feList, List<TravelDTO> travelList) {
//		println "feList size(): ${feList.size()}"
//		println ''
		
		feList.each {fe->
			travelList.each {t ->
				if (fe.fuelStart.odometer > t.departureOdometer && fe.fuelStart.odometer < t.arrivalOdometer ||
					t.departureOdometer > fe.fuelStart.odometer && t.arrivalOdometer < fe.fuelEnd.odometer ||
					fe.fuelEnd.odometer > t.departureOdometer && fe.fuelEnd.odometer < t.arrivalOdometer
					) {
					fe.travelList << t
				}
			}
		}
	}
	
	def calculateAdditionalValues(List<FuelEconomyDTO> feList) {
		BigDecimal oneHundred = new BigDecimal(100)
		MathContext mc = new MathContext(8)
		
		feList.each {fe->
			fe.travelList.each {t ->
				BigDecimal departureOdometer = Math.max(fe.fuelStart.odometer, t.departureOdometer)
				BigDecimal arrivalOdometer = Math.min(fe.fuelEnd.odometer, t.arrivalOdometer)
				fe.caravanKilometres = fe.caravanKilometres.add(arrivalOdometer.subtract(departureOdometer))
			}
			fe.vehicleKilometres = fe.vehicleKilometres.add(fe.fuelEnd.odometer).subtract(fe.fuelStart.odometer).subtract(fe.caravanKilometres)
			fe.totalKilometres = fe.vehicleKilometres.add(fe.caravanKilometres)
			fe.fuelEconomy = fe.fuelEnd.litres.multiply(oneHundred, mc).divide(fe.totalKilometres, mc)
		}
	}
	
}

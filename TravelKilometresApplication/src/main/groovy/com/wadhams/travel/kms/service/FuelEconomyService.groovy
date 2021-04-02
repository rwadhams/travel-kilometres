package com.wadhams.travel.kms.service

import java.text.NumberFormat
import java.text.SimpleDateFormat
import com.wadhams.travel.kms.dto.DepartureArrivalPair
import com.wadhams.travel.kms.dto.FuelEconomyDTO
import com.wadhams.travel.kms.dto.TravelKilometerDTO
import com.wadhams.travel.kms.type.Activity

class FuelEconomyService {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")

	BigDecimal oneHundred = new BigDecimal(100)
	
	List<FuelEconomyDTO> buildFuelEconomyList(List<TravelKilometerDTO> tkList, String afterDate ) {
		Date d = sdf.parse(afterDate)
		
		List<FuelEconomyDTO> feList = []
		
		assert tkList.size() >= 2
		
		for (int i; i < tkList.size()-1; i++) {
			if (tkList[i].activityDate.after(d)) {
				FuelEconomyDTO dto = new FuelEconomyDTO()
				
				dto.fuelStart = tkList[i]
				dto.fuelEnd = tkList[i+1]
				
				feList << dto
			}
		}

		return feList
	}
	
	List<DepartureArrivalPair> buildDepartureArrivalPairList(List<TravelKilometerDTO> tkList) {
		
		List<DepartureArrivalPair> dapList = []
			
		for (int i; i<tkList.size();i+=2) {
			DepartureArrivalPair dap = new DepartureArrivalPair()
			dap.departure = tkList[i]
			dap.arrival = tkList[i+1]
			dapList << dap
		}
		
		return dapList
	}

	def addCaravanTripsFuelEconomyList(List<FuelEconomyDTO> feList, List<DepartureArrivalPair> dapList) {
//		println "feList size(): ${feList.size()}"
//		println ''
		
		feList.each {fe->
			dapList.each {dap ->
				if (fe.fuelStart.odometer > dap.departure.odometer && fe.fuelStart.odometer < dap.arrival.odometer ||
					dap.departure.odometer > fe.fuelStart.odometer && dap.arrival.odometer < fe.fuelEnd.odometer ||
					fe.fuelEnd.odometer > dap.departure.odometer && fe.fuelEnd.odometer < dap.arrival.odometer
					) {
					fe.dapList << dap
				}
			}
		}
	}
	
	def calculateCaravanVehicleKilometres(List<FuelEconomyDTO> feList) {
		feList.each {fe->
			fe.dapList.each {dap ->
				BigDecimal departureOdometer = Math.max(fe.fuelStart.odometer ,dap.departure.odometer)
				BigDecimal arrivalOdometer = Math.min(fe.fuelEnd.odometer ,dap.arrival.odometer)
				fe.caravanKilometres = fe.caravanKilometres.add(arrivalOdometer.subtract(departureOdometer))
			}
			fe.vehicleKilometres = fe.vehicleKilometres.add(fe.fuelEnd.odometer).subtract(fe.fuelStart.odometer).subtract(fe.caravanKilometres)
		}
	}
	
	List<String> buildReport(List<FuelEconomyDTO> feList) {
		List<String> reportList = []

		List<BigDecimal> clphList = buildCaravanLitrePerHundredList()
		
		feList.each {fe ->
			BigDecimal caravanHundreds = fe.caravanKilometres.divide(oneHundred)
			//println caravanHundreds
			
			reportList << "${sdf.format(fe.fuelStart.activityDate)} (${fe.fuelStart.odometer}kms) - ${sdf.format(fe.fuelEnd.activityDate)} (${fe.fuelEnd.odometer}kms) - ${fe.fuelEnd.litres} litres - Caravan: ${fe.caravanKilometres}kms - Vehicle: ${fe.vehicleKilometres}kms (${fe.fuelEnd.odometer.subtract(fe.fuelStart.odometer)}kms)"

			String travels = "Caravan travels: ${fe.dapList.size()} "
			fe.dapList.each {dap ->
				travels += "(${dap.departure.odometer}-${dap.arrival.odometer}) "
			}
			reportList << travels

			String fuelEconomy
			if (fe.vehicleKilometres == BigDecimal.ZERO) {
				BigDecimal clph = fe.fuelEnd.litres.multiply(oneHundred).divide(fe.caravanKilometres,2)	//caravanLitresPerHundred
				fuelEconomy = "Caravan Fuel Economy: $clph"
			}
			else if (fe.caravanKilometres == BigDecimal.ZERO) {
				BigDecimal vlph = fe.fuelEnd.litres.multiply(oneHundred).divide(fe.vehicleKilometres,2)	//vehicleLitresPerHundred
				fuelEconomy = "Vehicle Fuel Economy: $vlph"
			}
			else {
				fuelEconomy = "Caravan - Vehicle Fuel Economy: "
				clphList.each {clph ->
					BigDecimal caravanLitres = caravanHundreds.multiply(clph)
					BigDecimal vehicleLitres = fe.fuelEnd.litres.subtract(caravanLitres)
					BigDecimal vlph = vehicleLitres.multiply(oneHundred).divide(fe.vehicleKilometres,2)	//vehicleLitresPerHundred
					fuelEconomy += "($clph - $vlph) "
				}
			}
			reportList << fuelEconomy
			reportList << ''
		}
		
		return reportList
	}
	
	List<BigDecimal> buildCaravanLitrePerHundredList() {
		List<BigDecimal> clphList = []
		
		clphList << new BigDecimal(18.0)
		clphList << new BigDecimal(18.5)
		clphList << new BigDecimal(19.0)
		clphList << new BigDecimal(19.5)
		clphList << new BigDecimal(20.0)
		clphList << new BigDecimal(20.5)
		clphList << new BigDecimal(21.0)
		clphList << new BigDecimal(21.5)
		clphList << new BigDecimal(22.0)
		clphList << new BigDecimal(22.5)
		clphList << new BigDecimal(23.0)
		clphList << new BigDecimal(23.5)
		clphList << new BigDecimal(24.0)
		clphList << new BigDecimal(24.5)
		
//		clphList.each {clph ->
//			println clph
//		}
//		println ''
		
		return clphList
	}
	
	Map<BigDecimal, List<BigDecimal>> buildPivotData(List<FuelEconomyDTO> feList) {
		Map<BigDecimal, List<BigDecimal>> map = [:]
		
		List<BigDecimal> clphList = buildCaravanLitrePerHundredList()
		//initialise map with empty lists
		clphList.each {clph ->
			map[clph] = []
		}

		feList.each {fe ->
			if (fe.vehicleKilometres != BigDecimal.ZERO && fe.caravanKilometres != BigDecimal.ZERO && fe.fuelEnd.litres > 60.0) {
				BigDecimal caravanHundreds = fe.caravanKilometres.divide(oneHundred)
				clphList.each {clph ->
					BigDecimal caravanLitres = caravanHundreds.multiply(clph)
					BigDecimal vehicleLitres = fe.fuelEnd.litres.subtract(caravanLitres)
					BigDecimal vlph = vehicleLitres.multiply(oneHundred).divide(fe.vehicleKilometres,2)	//vehicleLitresPerHundred
					map[clph] << vlph
				}
			}
		}

		return map
	}
	
	List<String> buildPivotReport(Map<BigDecimal, List<BigDecimal>> map) {
		List<String> reportList = []

		NumberFormat nf1 = NumberFormat.getInstance()
		nf1.setMinimumFractionDigits(1)
		
		map.each {k,v ->
			BigDecimal average = new BigDecimal(0) 
			v.each {
				average = average.add(it)
			}
			average = average.divide(v.size(), 2)
			
			String s = "${nf1.format(k)}\tAvg: ${nf1.format(average)}\t"
			
			v.each {
				s += "$it\t"
			}
			reportList << s
		}
		
		return reportList
	}

}

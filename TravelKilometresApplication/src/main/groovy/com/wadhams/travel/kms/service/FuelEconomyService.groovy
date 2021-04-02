package com.wadhams.travel.kms.service

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
				fe.caravanKilometres = fe.caravanKilometres.add(dap.arrival.odometer.subtract(dap.departure.odometer))
			}
			fe.vehicleKilometres = fe.vehicleKilometres.add(fe.fuelEnd.odometer).subtract(fe.fuelStart.odometer).subtract(fe.caravanKilometres)
		}
	}
	
	List<String> report(FuelEconomyDTO fe, List<BigDecimal> clphList) {
		List<String> reportList = []
		reportList << "Caravan\t\tVehicle"
		reportList << "ltrs/100\tltrs/100"
		
		BigDecimal caravanHundreds = fe.caravanKilometres.divide(oneHundred)
		//println caravanHundreds
		
		clphList.each {clph ->
			BigDecimal caravanLitres = caravanHundreds.multiply(clph)
			BigDecimal vehicleLitres = fe.fuelEnd.litres.subtract(caravanLitres)
			BigDecimal vlph = vehicleLitres.multiply(oneHundred).divide(fe.vehicleKilometres,2)	//vehicleLitresPerHundred
			//reportList << "$clph\t$caravanLitres\t$vehicleLitres\t$vehicleLitresPerHundred"
			reportList << "$clph\t\t$vlph"
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
	
}

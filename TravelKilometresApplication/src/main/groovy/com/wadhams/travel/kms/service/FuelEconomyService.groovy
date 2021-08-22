package com.wadhams.travel.kms.service

import java.text.NumberFormat
import java.text.SimpleDateFormat
import com.wadhams.travel.kms.comparator.FuelEconomyDateComparator
import com.wadhams.travel.kms.comparator.FuelEconomyPerformanceComparator
import com.wadhams.travel.kms.dto.FuelDTO
import com.wadhams.travel.kms.dto.FuelEconomyDTO
import com.wadhams.travel.kms.dto.TravelDTO

class FuelEconomyService {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")

	BigDecimal oneHundred = new BigDecimal(100)
	
	List<FuelEconomyDTO> buildFuelEconomyList(List<FuelDTO> fuelList, String afterDate) {
		Date d = sdf.parse(afterDate)
		
		List<FuelEconomyDTO> feList = []
		
		assert fuelList.size() >= 2
		
		for (int i; i < fuelList.size()-1; i++) {
			if (fuelList[i].fuelDate.after(d)) {
				FuelEconomyDTO dto = new FuelEconomyDTO()
				
				dto.fuelStart = fuelList[i]
				dto.fuelEnd = fuelList[i+1]
				
				feList << dto
			}
		}

		return feList
	}
	
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
		feList.each {fe->
			fe.travelList.each {t ->
				BigDecimal departureOdometer = Math.max(fe.fuelStart.odometer, t.departureOdometer)
				BigDecimal arrivalOdometer = Math.min(fe.fuelEnd.odometer, t.arrivalOdometer)
				fe.caravanKilometres = fe.caravanKilometres.add(arrivalOdometer.subtract(departureOdometer))
			}
			fe.vehicleKilometres = fe.vehicleKilometres.add(fe.fuelEnd.odometer).subtract(fe.fuelStart.odometer).subtract(fe.caravanKilometres)
			fe.totalKilometres = fe.vehicleKilometres.add(fe.caravanKilometres)
			fe.fuelEconomy = fe.fuelEnd.litres.multiply(100).divide(fe.totalKilometres, 2)
		}
	}
	
	def reportByDate(List<FuelEconomyDTO> feList) {
		File f = new File("out/fuel-economy-date-report.txt")
		
		f.withPrintWriter {pw ->
			pw.println 'FUEL ECONOMY REPORT (sorted by Date)'
			pw.println '------------------------------------'
			
			Collections.sort(feList, new FuelEconomyDateComparator())
	
			report(feList, pw)
		}
	}

	def reportByPerformance(List<FuelEconomyDTO> feList) {
		File f = new File("out/fuel-economy-performance-report.txt")
		
		f.withPrintWriter {pw ->
			pw.println 'FUEL ECONOMY REPORT (sorted by Performance)'
			pw.println '-------------------------------------------'
			
			Collections.sort(feList, new FuelEconomyPerformanceComparator())
	
			report(feList, pw)
		}
	}

	def report(List<FuelEconomyDTO> feList, PrintWriter pw) {
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		nf.setGroupingUsed(false)
		NumberFormat nf2 = NumberFormat.getNumberInstance()
		nf2.setMaximumFractionDigits(2)
		nf2.setMinimumFractionDigits(2)
		
		feList.each {fe ->
			String s1 = sdf.format(fe.fuelStart.fuelDate)
			String s2 = nf2.format(fe.fuelEnd.litres).padRight(6, ' ')
			String s3 = nf.format(fe.caravanKilometres).padLeft(3, ' ')
			String s4 = nf.format(fe.vehicleKilometres).padLeft(4, ' ')
			String s5 = nf.format(fe.totalKilometres).padLeft(4, ' ')
			String s6 = nf2.format(fe.fuelEconomy)

			
			pw.println "$s1 $s2 litres, Caravan: ${s3}kms, Vehicle: ${s4}kms, Total: ${s5}kms litres/100km: $s6"
		}
	}
	
	List<String> buildReport(List<FuelEconomyDTO> feList) {
		List<String> reportList = []

		List<BigDecimal> clphList = buildCaravanLitrePerHundredList()
		
		feList.each {fe ->
			BigDecimal caravanHundreds = fe.caravanKilometres.divide(oneHundred)
			//println caravanHundreds
			
			reportList << "${sdf.format(fe.fuelStart.fuelDate)} (${fe.fuelStart.odometer}kms) - ${sdf.format(fe.fuelEnd.fuelDate)} (${fe.fuelEnd.odometer}kms) - ${fe.fuelEnd.litres} litres - Caravan: ${fe.caravanKilometres}kms - Vehicle: ${fe.vehicleKilometres}kms (${fe.fuelEnd.odometer.subtract(fe.fuelStart.odometer)}kms)"

			String travels = "Caravan travels: ${fe.travelList.size()} "
			fe.travelList.each {t ->
				travels += "(${t.departureOdometer}-${t.arrivalOdometer}) "
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
		clphList << new BigDecimal(25.0)
		clphList << new BigDecimal(25.5)
		clphList << new BigDecimal(26.0)
		clphList << new BigDecimal(26.5)

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

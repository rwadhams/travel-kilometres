package com.wadhams.travel.kms.report

import java.text.NumberFormat
import java.text.SimpleDateFormat

import com.wadhams.travel.kms.dto.DepartureArrivalPair
import com.wadhams.travel.kms.dto.TravelKilometerDTO
import com.wadhams.travel.kms.type.Activity

class TravelReportService {
	def execute(List<TravelKilometerDTO> tkList) {
		File f = new File("out/travel-report.txt")
		
		f.withPrintWriter {pw ->
			pw.println 'TRAVEL REPORT'
			pw.println '-------------'
	
			report(tkList, pw)
		}
	}
	
	def report(List<TravelKilometerDTO> tkList, PrintWriter pw) {
		List<DepartureArrivalPair> list = []
				
		for (int i; i<tkList.size();i+=2) {
			DepartureArrivalPair dap = new DepartureArrivalPair()
			dap.departure = tkList[i]
			dap.arrival = tkList[i+1]
			list << dap
		}
		
		int maxDepartureLocationSize = maxLocationSize(tkList, Activity.Departure)
		//println "maxDepartureLocationSize...: $maxDepartureLocationSize"
		int maxArrivalLocationSize = maxLocationSize(tkList, Activity.Arrival)
		//println "maxArrivalLocationSize.....: $maxArrivalLocationSize"

		report(list, maxDepartureLocationSize, maxArrivalLocationSize, pw)
	}
	
	def report(List<DepartureArrivalPair> dapList, int maxDepartureLocationSize, int maxArrivalLocationSize, PrintWriter pw) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)

		BigDecimal totalCaravanKms = new BigDecimal(0.0)
		BigDecimal totalVehicleOnlyKms = new BigDecimal(0.0)
		
		DepartureArrivalPair previous = null

		dapList.each {dap ->
			TravelKilometerDTO departure = dap.departure
			TravelKilometerDTO arrival = dap.arrival
			
			//vehicle kilometers when a previous DepartureArrivalPair exists
			if (previous) {
				BigDecimal vehicleOnlyKms = departure.odometer.subtract(previous.arrival.odometer)
				if (vehicleOnlyKms == 0) {
					pw.println "Overnight in ${previous.arrival.location}"
					pw.println ''
				}
				else {
					pw.println "Vehicle kilometers around ${previous.arrival.location}: ${nf.format(vehicleOnlyKms)}"
					pw.println ''
					totalVehicleOnlyKms = totalVehicleOnlyKms.add(vehicleOnlyKms)
				}
			}
			
			BigDecimal caravanKms = arrival.odometer.subtract(departure.odometer)
			String departureLocation = departure.location.padRight(maxDepartureLocationSize, ' ')
			String arrivalLocation = arrival.location.padRight(maxArrivalLocationSize, ' ')
			pw.println "${sdf.format(departure.activityDate)}  $departureLocation$arrivalLocation  ${nf.format(departure.odometer).padLeft(9, ' ')}   ${nf.format(arrival.odometer).padLeft(9, ' ')}   ${nf.format(caravanKms).padLeft(7, ' ')}   ${arrival.campsite}"
			
			totalCaravanKms = totalCaravanKms.add(caravanKms)
			
			previous = dap
		}
		
		pw.println ''
		pw.println "Total caravan kilometers........: ${nf.format(totalCaravanKms).padLeft(8, ' ')}"
		pw.println "Total vehicle-only kilometers...: ${nf.format(totalVehicleOnlyKms).padLeft(8, ' ')}"
	}

	def report32(List<TravelKilometerDTO> tkList, PrintWriter pw) {
		BigDecimal totalCaravanKms = new BigDecimal(0.0)
		BigDecimal totalVehicleOnlyKms = new BigDecimal(0.0)
		
		BigDecimal totalLitres = new BigDecimal(0.0)
		BigDecimal totalDollars = new BigDecimal(0.0)
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
		NumberFormat cf = NumberFormat.getCurrencyInstance()
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		NumberFormat nf2 = NumberFormat.getNumberInstance()
		nf2.setMaximumFractionDigits(2)
		nf2.setMinimumFractionDigits(2)
		NumberFormat nf3 = NumberFormat.getNumberInstance()
		nf3.setMaximumFractionDigits(3)

		TravelKilometerDTO previousDTO = tkList[0]
		pw.println "${sdf.format(previousDTO.activityDate)}  Starting Odometer: ${nf.format(previousDTO.odometer)}"
		pw.println ''
		
		pw.println "                       Dollars                         Litres"
		pw.println "Transaction             per                             per"
		pw.println "Date         Odometer  Litre    Dollars  Litres  Km's  100km "
		pw.println "-----------  --------  -------  -------  ------  ----  ------"
		
		tkList[1..-1].each {dto ->
			BigDecimal kilometres = dto.odometer.subtract(previousDTO.odometer)
			totalKms = totalKms.add(kilometres)
			totalLitres = totalLitres.add(dto.litres)
			
			String s1 = sdf.format(dto.activityDate).padRight(13, ' ')
			
			String s2 = nf.format(dto.odometer).padRight(10, ' ')
			
			String s3 = nf3.format(dto.dollarsPerLitre).padRight(9, ' ')
			
			BigDecimal dollars = dto.dollarsPerLitre.multiply(dto.litres)
			String s4 = cf.format(dollars).padRight(9, ' ')
			totalDollars = totalDollars.add(dollars)
			
			String s5 = nf2.format(dto.litres).padRight(8, ' ')
			
			String s6 = nf.format(kilometres).padRight(6, ' ')
			
			BigDecimal economy = dto.litres.multiply(100).divide(kilometres, 2)
			String s7 = nf2.format(economy)
			
			pw.println "$s1$s2$s3$s4$s5$s6$s7"
			
			previousDTO = dto
		}
		
		pw.println ''
		pw.println "Total Kilometers: ${nf.format(totalKms)}"
		pw.println "Total Litres....: ${nf2.format(totalLitres)}"
		pw.println "Total Dollars...: ${cf.format(totalDollars)}"
	}
	
	int maxLocationSize(List<TravelKilometerDTO> tkList, Activity activity) {
		int maxLocationSize = 0
		tkList.each {tk ->
			if (tk.activity == activity && tk.location.size() > maxLocationSize) {
				maxLocationSize = tk.location.size()
			}
		}
		return maxLocationSize
	}
}

package com.wadhams.travel.kms.report

import java.text.NumberFormat
import java.text.SimpleDateFormat

import com.wadhams.travel.kms.dto.DepartureArrivalPair
import com.wadhams.travel.kms.dto.TravelKilometerDTO
import com.wadhams.travel.kms.type.Activity
import java.math.MathContext

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
		
		Date startingDate = tkList[0].activityDate
		
		int maxDepartureLocationSize = maxLocationSize(tkList, Activity.Departure)
		//println "maxDepartureLocationSize...: $maxDepartureLocationSize"
		int maxArrivalLocationSize = maxLocationSize(tkList, Activity.Arrival)
		//println "maxArrivalLocationSize.....: $maxArrivalLocationSize"

		report(list, startingDate, maxDepartureLocationSize, maxArrivalLocationSize, pw)
	}
	
	def report(List<DepartureArrivalPair> dapList, Date startingDate, int maxDepartureLocationSize, int maxArrivalLocationSize, PrintWriter pw) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		NumberFormat pf = NumberFormat.getPercentInstance()
		pf.setMaximumFractionDigits(2)

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
		
		BigDecimal combinedKilometers = totalCaravanKms.add(totalVehicleOnlyKms)
		BigDecimal caravanPercentage = totalCaravanKms.divide(combinedKilometers, MathContext.DECIMAL64)
		BigDecimal vehicleOnlyPercentage = totalVehicleOnlyKms.divide(combinedKilometers, MathContext.DECIMAL64)
		
		pw.println ''
		pw.println "Total caravan kilometers........: ${nf.format(totalCaravanKms).padLeft(8, ' ')} (${pf.format(caravanPercentage)})"
		pw.println "Total vehicle-only kilometers...: ${nf.format(totalVehicleOnlyKms).padLeft(8, ' ')} (${pf.format(vehicleOnlyPercentage)})"
		
		pw.println ''
		pw.println "${nf.format(combinedKilometers)} Kms (combined caravan and vehicle) since: ${sdf.format(startingDate)}  (i.e. Caravan pickup in Melbourne)"
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

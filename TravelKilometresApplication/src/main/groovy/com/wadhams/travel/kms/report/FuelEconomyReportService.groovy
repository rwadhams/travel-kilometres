package com.wadhams.travel.kms.report

import java.text.NumberFormat
import java.text.SimpleDateFormat
import com.wadhams.travel.kms.comparator.FuelEconomyDateComparator
import com.wadhams.travel.kms.comparator.FuelEconomyPerformanceComparator
import com.wadhams.travel.kms.dto.FuelDTO
import com.wadhams.travel.kms.dto.FuelEconomyDTO

class FuelEconomyReportService {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")

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
			String s1 = sdf.format(fe.fuelEnd.fuelDate)
			String s2 = nf2.format(fe.fuelEnd.litres).padRight(6, ' ')
			String s3 = nf.format(fe.caravanKilometres).padLeft(3, ' ')
			String s4 = nf.format(fe.vehicleKilometres).padLeft(4, ' ')
			String s5 = nf.format(fe.totalKilometres).padLeft(4, ' ')
			String s6 = nf2.format(fe.fuelEconomy)

			
			pw.println "$s1 $s2 litres, Caravan: ${s3}kms, Vehicle: ${s4}kms, Total: ${s5}kms litres/100km: $s6"
		}
	}
}

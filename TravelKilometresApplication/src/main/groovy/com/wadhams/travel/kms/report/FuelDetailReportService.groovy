package com.wadhams.travel.kms.report

import java.math.MathContext
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import com.wadhams.travel.kms.dto.FuelDTO

class FuelDetailReportService {
	def execute(List<FuelDTO> fuelList) {
		File f = new File("out/fuel-detail-report.txt")
		
		f.withPrintWriter {pw ->
			pw.println 'FUEL DETAIL REPORT'
			pw.println '------------------'
	
			report(fuelList, pw)
		}
	}
	
	def report(List<FuelDTO> fuelList, PrintWriter pw) {
		BigDecimal totalKms = new BigDecimal(0.0)
		BigDecimal totalLitres = new BigDecimal(0.0)
		BigDecimal totalDollars = new BigDecimal(0.0)
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
		NumberFormat cf = NumberFormat.getCurrencyInstance()
		NumberFormat nf = NumberFormat.getNumberInstance()
		nf.setMaximumFractionDigits(0)
		NumberFormat nf2 = NumberFormat.getNumberInstance()
		nf2.setMaximumFractionDigits(2)
		nf2.setMinimumFractionDigits(2)
		NumberFormat nf3 = NumberFormat.getNumberInstance()
		nf3.setMaximumFractionDigits(3)
		
		BigDecimal oneHundred = new BigDecimal(100)
		MathContext mc = new MathContext(8)
		
		FuelDTO previousDTO = fuelList[0]
		pw.println "${previousDTO.fuelDate.format(dtf)}  Starting Odometer: ${nf.format(previousDTO.odometer)}"
		pw.println ''
		
		pw.println "                       Dollars                         Litres"
		pw.println "Transaction             per                             per"
		pw.println "Date         Odometer  Litre    Dollars  Litres  Km's  100km "
		pw.println "-----------  --------  -------  -------  ------  ----  ------"
		
		fuelList[1..-1].each {dto ->
			BigDecimal kilometres = dto.odometer.subtract(previousDTO.odometer)
			totalKms = totalKms.add(kilometres)
			totalLitres = totalLitres.add(dto.litres)
			
			String s1 = dto.fuelDate.format(dtf).padRight(13, ' ')
			
			String s2 = nf.format(dto.odometer).padRight(10, ' ')
			
			String s3 = nf3.format(dto.dollarsPerLitre).padRight(9, ' ')
			
			BigDecimal dollars = dto.dollarsPerLitre.multiply(dto.litres)
			String s4 = cf.format(dollars).padRight(9, ' ')
			totalDollars = totalDollars.add(dollars)
			
			String s5 = nf2.format(dto.litres).padRight(8, ' ')
			
			String s6 = nf.format(kilometres).padRight(6, ' ')
			
			BigDecimal economy = dto.litres.multiply(oneHundred, mc).divide(kilometres, mc)
			String s7 = nf2.format(economy)
			
			pw.println "$s1$s2$s3$s4$s5$s6$s7"
			
			previousDTO = dto
		}
		
		pw.println ''
		pw.println "Total Kilometers: ${nf.format(totalKms)}"
		pw.println "Total Litres....: ${nf2.format(totalLitres)}"
		pw.println "Total Dollars...: ${cf.format(totalDollars)}"
	}
	
}

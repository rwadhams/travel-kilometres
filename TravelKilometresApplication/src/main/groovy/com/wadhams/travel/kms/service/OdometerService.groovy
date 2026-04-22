package com.wadhams.travel.kms.service

import static com.wadhams.travel.kms.type.Vehicle.KimberleyKamper
import static com.wadhams.travel.kms.type.Vehicle.SaluteCaravan
import static com.wadhams.travel.kms.type.Vehicle.ToyotaLandCruiser

import com.wadhams.travel.kms.biz.OdometerMap
import com.wadhams.travel.kms.dto.TravelDTO

class OdometerService {
	OdometerMap buildOdometers(List<TravelDTO> travelList) {
		OdometerMap odometerMap = new OdometerMap()
		
		BigDecimal odometerReading
		
		//ToyotaLandCruiser
		odometerReading = new BigDecimal(0.0)
		travelList.each {t ->
			if (t.vehicle == ToyotaLandCruiser && t.arrivalOdometer > odometerReading) {
				odometerReading = t.arrivalOdometer
			}
		}
		odometerMap.addOdometer(ToyotaLandCruiser, odometerReading)
		
		//SaluteCaravan
		odometerReading = new BigDecimal(0.0)
		travelList.each {t ->
			if (t.trailer == SaluteCaravan) {
				BigDecimal trailerKms = t.arrivalOdometer.subtract(t.departureOdometer)
				odometerReading = odometerReading.add(trailerKms)
			}
		}
		odometerMap.addOdometer(SaluteCaravan, odometerReading)

		//KimberleyKamper
		odometerReading = new BigDecimal(0.0)
		travelList.each {t ->
			if (t.trailer == KimberleyKamper) {
				BigDecimal trailerKms = t.arrivalOdometer.subtract(t.departureOdometer)
				odometerReading = odometerReading.add(trailerKms)
			}
		}
		odometerMap.addOdometer(KimberleyKamper, odometerReading)
		
		return odometerMap
	}
}

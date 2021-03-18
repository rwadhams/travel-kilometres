package com.wadhams.travel.kms.service

import java.text.SimpleDateFormat
import com.wadhams.travel.kms.dto.FuelEconomyDTO
import com.wadhams.travel.kms.dto.TravelKilometerDTO
import com.wadhams.travel.kms.type.Activity

class FuelEconomyService {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy")

	List<FuelEconomyDTO> build(List<TravelKilometerDTO> tkList, String afterDate ) {
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
	
	def augmentFuelEconomyList(List<FuelEconomyDTO> feList, List<TravelKilometerDTO> travelList) {
		feList.each {fe->
			travelList.each {t ->
				if (t.odometer > fe.fuelStart.odometer && t.odometer < fe.fuelEnd.odometer) {
					fe.travelList << t
				}
			}
		}
	}
	
}

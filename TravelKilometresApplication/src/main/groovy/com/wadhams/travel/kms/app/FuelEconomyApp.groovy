package com.wadhams.travel.kms.app

import com.wadhams.travel.kms.controller.FuelEconomyController
import com.wadhams.travel.kms.controller.TravelKilometresController

class FuelEconomyApp {
	static main(args) {
		println 'FuelEconomyApp started...'
		println ''

		FuelEconomyController controller = new FuelEconomyController()
		controller.execute()
		
		println ''
		println 'FuelEconomyApp ended.'
	}
}

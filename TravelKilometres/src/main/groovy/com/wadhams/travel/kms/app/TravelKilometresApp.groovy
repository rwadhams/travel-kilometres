package com.wadhams.travel.kms.app

import com.wadhams.travel.kms.controller.TravelKilometresController

class TravelKilometresApp {
	static main(args) {
		println 'TravelKilometresApp started...'
		println ''

		TravelKilometresController controller = new TravelKilometresController()
		controller.execute()
		
		println ''
		println 'TravelKilometresApp ended.'
	}
}

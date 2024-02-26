package com.wadhams.travel.kms.app

import com.wadhams.travel.kms.controller.TravelKilometresController

class TravelKilometresTestApp {

	static void main(args) {
		println 'TravelKilometresTestApp started...'
		println ''

		TravelKilometresController controller = new TravelKilometresController()
		controller.execute()
		
		println ''
		println 'TravelKilometresTestApp ended.'

	}
}

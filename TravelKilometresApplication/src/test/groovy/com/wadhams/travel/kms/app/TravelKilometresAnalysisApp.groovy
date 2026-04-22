package com.wadhams.travel.kms.app

import com.wadhams.travel.kms.controller.TravelKilometresAnalyser
import com.wadhams.travel.kms.controller.TravelKilometresController

class TravelKilometresAnalysisApp {

	static void main(args) {
		println 'TravelKilometresAnalysisApp started...'
		println ''

		TravelKilometresAnalyser analyser = new TravelKilometresAnalyser()
		analyser.execute()
		
		println ''
		println 'TravelKilometresAnalysisApp ended.'

	}
}

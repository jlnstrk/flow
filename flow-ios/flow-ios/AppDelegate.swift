//
//  AppDelegate.swift
//  flow-ios
//
//  Created by Julian Ostarek on 10.06.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import UIKit
import FirebaseCore
import GoogleMaps

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()

        let mapsKey = Bundle.main.object(forInfoDictionaryKey: "GMS_MAPS_KEY") as? String
        guard let mapsKey = mapsKey, !mapsKey.isEmpty else {
            print("Missing GMS_MAPS_KEY")
            return false
        }
        GMSServices.provideAPIKey(mapsKey)
        return true
    }
}

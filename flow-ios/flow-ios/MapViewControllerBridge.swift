//
//  MapViewControllerBridge.swift
//  flow-ios
//
//  Created by Julian Ostarek on 10.06.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

import GoogleMaps
import SwiftUI

struct MapViewControllerBridge: UIViewControllerRepresentable {

  func makeUIViewController(context: Context) -> MapViewController {
    return MapViewController()
  }

  func updateUIViewController(_ uiViewController: MapViewController, context: Context) {
  }
}

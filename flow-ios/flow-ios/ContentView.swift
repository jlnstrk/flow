import SwiftUI
import Shared

struct ContentView: View {
    @State var journeys: [Journey] = []
    @State var isPresented = true

    var body: some View {
        if #available(iOS 16.0, *) {
            NavigationStack {
                ZStack {
                    MapViewControllerBridge()
                        .edgesIgnoringSafeArea(.all)
                        .sheet(isPresented: $isPresented) {
                            List {
                                ForEach(journeys) { journey in
                                    VStack {
                                        Text("\(journey.line.label) to \(journey.directionTo!.name!)")
                                        /*let departureScheduled = (journey.stop as! Stop.Departure).departureScheduled
                                        Text(departureScheduled.description())*/
                                    }
                                }
                            }
                            .presentationDetents([.medium, .large])
                        }
                }
            }
            .task {
                let stationBoardService = Shared.MvvEfa.shared.optional(serviceProtocol: Shared.StationBoardService.self) as! StationBoardService
                print("CHECKPOINT")
                let location = Location.Point(coordinates: Coordinates(
                    latitude: 48.135124,
                    longitude: 11.581981,
                    altitude: nil))
                stationBoardService.stationBoard(
                    mode: .departures,
                    location: location,
                    direction: nil,
                    dateTime: nil,
                    filterProducts: nil,
                    filterLines: nil,
                    maxDuration: nil,
                    maxResults: nil
                ) { result, err  in
                    print(result)
                    if let unwrapped = result,
                       let serviceSuccess = unwrapped as? ServiceResultSuccess<StationBoardData>,
                       let data = serviceSuccess.result as? StationBoardData {
                        journeys = data.journeys
                    }
                }
            }
        } else {
            Text("Hello World!")
        }
    }
}

extension Journey : Swift.Identifiable {
    public var id: UInt {
        self.hash()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

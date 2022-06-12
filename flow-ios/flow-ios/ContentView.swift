import SwiftUI
import Shared
import BottomSheet

enum BottomSheetRelativePosition: CGFloat, CaseIterable {
    case bottom = 0.216
    case middle = 0.355
    case top = 0.829
}

struct VisualEffectView: UIViewRepresentable {
    var effect: UIVisualEffect?
    func makeUIView(context: UIViewRepresentableContext<Self>) -> UIVisualEffectView { UIVisualEffectView() }
    func updateUIView(_ uiView: UIVisualEffectView, context: UIViewRepresentableContext<Self>) { uiView.effect = effect }
}

extension UIApplication {
    static var statusBarHeight: CGFloat {
        let viewController = UIApplication.shared.windows.first!.rootViewController
        return viewController!.view.window?.windowScene?.statusBarManager?.statusBarFrame.height ?? 0
    }
}

struct ContentView: View {
    @State var journeys: [Journey] = []
    @State var position: BottomSheetRelativePosition = .middle

    let backgroundColors: [Color] = [Color(red: 0.28, green: 0.28, blue: 0.53), Color(red: 1, green: 0.69, blue: 0.26)]

    var body: some View {
        NavigationView {
            ZStack(alignment: .top) {
                MapViewControllerBridge()
                    .edgesIgnoringSafeArea(.all)
                VisualEffectView(effect: UIBlurEffect(style: .regular))
                    .frame(width: UIScreen.main.bounds.width, height: UIApplication.statusBarHeight, alignment: .top)
                    .edgesIgnoringSafeArea(.top)
                BottomSheetView(
                    position: $position,
                    header: {
                        VStack(spacing: 0) {
                            Button(action: {
                                if position != .top {
                                    position = .top
                                } else {
                                    position = .middle
                                }
                            }, label: {
                                Rectangle()
                                    .frame(
                                        width: 36,
                                        height: 5,
                                        alignment: .center
                                    )
                                    .foregroundColor(Color(UIColor.systemGray3))
                                    .cornerRadius(2.5)
                            })

                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Business News")
                                        .font(.title)
                                        .fontWeight(.heavy)
                                    Text("From Yahoo Finance")
                                        .foregroundColor(Color(UIColor.secondaryLabel))
                                }
                                .padding(.top, 10)
                                .padding(.bottom, 16)

                                Spacer()
                            }

                            Divider()
                             .frame(height: 1)
                             .background(Color(UIColor.systemGray6))
                        }
                        .padding(.top, 8)
                        .padding(.horizontal, 16)
                        .background(
                            Color(uiColor: UIColor.secondarySystemBackground)
                                .cornerRadius(12, corners: [.topLeft, .topRight])
                        )
                    },
                    content: {
                        VStack(spacing: 0) {
                            ScrollView {
                                ForEach(journeys) { journey in
                                    VStack {
                                        Text("\(journey.line.label) to \(journey.directionTo!.name!)")
                                        /*let departureScheduled = (journey.stop as! Stop.Departure).departureScheduled
                                         Text(departureScheduled.description())*/
                                    }
                                }
                            }


                            Spacer(minLength: 8)
                        }.frame(maxWidth: .infinity)
                        .background(Color(uiColor: UIColor.secondarySystemBackground))
                        // Enable or disable the following line if we want to run the scrollview outside the safe-area
                        .edgesIgnoringSafeArea([.bottom])
                    }
                )
                .animationCurve(mass: 1, stiffness: 250)
                .snapThreshold(1.8)
                .onBottomSheetDrag { translation in
                    print("Translation", translation)
                }
                .offset(y: 8)

//                VStack(spacing: 0) {
//                    Divider()
//                     .frame(height: 1)
//                     .background(Color(UIColor.systemGray6))
//
//                    HStack {
//                        Text("Yahoo Finance")
//                        Spacer()
//                    }
//                    .padding(.horizontal, 16)
//                    .padding(.vertical, 16)
//                }
//                .background(
//                    Color(UIColor.systemBackground)
//                        .edgesIgnoringSafeArea([.bottom])
//                )
//                .zIndex(1)
            }
            .background(
                Color(UIColor.systemFill)
                    .edgesIgnoringSafeArea([.all])
            )
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

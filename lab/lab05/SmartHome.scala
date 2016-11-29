// As a second example of abstract types we reimplement the smart home
// example of the lecture.
trait SmartHome {

  // We don't know how rooms are implemented (for now).
  type Room
  def rooms: List[Room]
}

trait Lights extends SmartHome {

  // This is an upper (type) bound. We express that we still don't know what
  // rooms are, but that they at least have to implement the `LightRoom`
  // interface.
  type Room <: LightRoom

  trait LightRoom {
    def addLight(l: Light): Unit
    def lights: List[Light]
  }

  trait Light {
    def turnOn(): Unit
    def turnOff(): Unit
  }

  // Not knowing the actual implementation of `Room` we still can already
  // implement methods using the fact that `Room <: LightRoom`.
  def turnOffAllLights(): Unit =
    for (room <- rooms; light <- room.lights) {
      light.turnOff()
    }
}

// A second trait very similar in structure to `Lights` just imposing a
// different upper bound on `Room` (which is `ShutterRoom` in this case).
trait Shutters extends SmartHome {

  type Room <: ShutterRoom

  trait ShutterRoom {
    def addShutter(l: Shutter): Unit
    def shutters: List[Shutter]
  }

  trait Shutter {
    def up(): Unit
    def down(): Unit
  }

  def allShuttersDown(): Unit =
    for (room <- rooms; shutter <- room.shutters) {
      shutter.down()
    }
}


// We can compose the two (possibly seperately developed) modules for
// lights and shutters by using mixin composition. Scala does not allow
// a trait or a class to inherit from more than one class. But inheriting
// from more than one `trait` is ok. Here we extend both Shutters and Lights
// to have access to both functions `allShuttersDown` and `turnOffAllLights`.
//
class ShuttersAndLights extends Shutters with Lights {

  type Room = LightRoom with ShutterRoom

  class MyRoom extends LightRoom with ShutterRoom {
    private var ls: List[Light] = Nil
    private var ss: List[Shutter] = Nil

    def addLight(l: Light): Unit = { ls = l :: ls }
    def lights: List[Light] = ls

    def addShutter(s: Shutter): Unit = { ss = s :: ss }
    def shutters: List[Shutter] = ss
  }

  // MyRoom extends both LightRoom and ShutterRoom, so
  //   MyRoom <: LightRoom with ShutterRoom
  // hence
  //   MyRoom <: Room
  val livingRoom: Room = new MyRoom
  val bathRoom: Room   = new MyRoom
  val bedRoom: Room    = new MyRoom

  def rooms = List(livingRoom, bathRoom, bedRoom)

  allShuttersDown()
  turnOffAllLights()

  // of course we can access `shutters` and `lights` on an instance
  // of `Room`.
  livingRoom.shutters
  livingRoom.lights
}

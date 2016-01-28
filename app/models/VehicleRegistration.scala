package models

import play.api.libs.json._

case class VehicleRegistration(id: Long, regNumber: String, vehicleMake: String, v5cDocRef: String)

object VehicleRegistration {

  implicit val vehicleRegistrationFormat = Json.format[VehicleRegistration]
}
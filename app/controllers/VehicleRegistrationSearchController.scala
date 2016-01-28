package controllers

import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import dal._

import scala.concurrent.{ ExecutionContext, Future }

import javax.inject._

class VehicleRegistrationSearchController @Inject()(repo: VehicleRegistrationRepository, val messagesApi: MessagesApi)
                                                   (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  val vehicleRegistrationSearchForm: Form[CreateVehicleRegistrationSearchForm] = Form {
    mapping(
      "regNumber" -> nonEmptyText,
      "vehicleMake" -> nonEmptyText,
      "v5cDocRef" -> text
    )(CreateVehicleRegistrationSearchForm.apply)(CreateVehicleRegistrationSearchForm.unapply)
  }

  def index = Action {
    Ok(views.html.index(vehicleRegistrationSearchForm))
  }

  def search = Action.async { implicit request =>
    vehicleRegistrationSearchForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm)))
      },
      // There were no errors in the from, so create the person.
      reg => {
        repo.find(reg.regNumber, reg.vehicleMake, reg.v5cDocRef).map { vehicle =>
          if(vehicle.isDefined)
            Ok(views.html.vehicle(vehicle.get))
          else
            Ok(views.html.vehicleNotFound())
        }
      }
    )
  }

  def getRegistrations = Action.async {
    repo.list().map { r =>
      Ok(Json.toJson(r))
    }
  }
}

case class CreateVehicleRegistrationSearchForm(regNumber: String, vehicleMake: String, v5cDocRef: String)

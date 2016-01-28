package dal

import javax.inject.{ Inject, Singleton }
import models.VehicleRegistration
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class VehicleRegistrationRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import driver.api._

  private class VehicleRegistrationTable(tag: Tag) extends Table[VehicleRegistration](tag, "vehicleregistration") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def regNumber = column[String]("regnumber")

    def vehicleMake = column[String]("vehiclemake")

    def v5cDocRef = column[String]("v5cdocref")

    def * = (id, regNumber, vehicleMake, v5cDocRef) <> ((VehicleRegistration.apply _).tupled, VehicleRegistration.unapply)
  }

  /**
   * The starting point for all queries on the people table.
   */
  private val vehicleRegistrations = TableQuery[VehicleRegistrationTable]

  private def filterQuery(regNumber: String, vehicleMake: String, v5cDocRef: String): Query[VehicleRegistrationTable, VehicleRegistration, Seq] =
    vehicleRegistrations.filter(r => r.regNumber === regNumber && r.vehicleMake === vehicleMake && r.v5cDocRef === v5cDocRef)

  private def filterQuery(regNumber: String, vehicleMake: String): Query[VehicleRegistrationTable, VehicleRegistration, Seq] =
    vehicleRegistrations.filter(r => r.regNumber === regNumber && r.vehicleMake === vehicleMake)

  def find(regNumber: String, vehicleMake: String, v5cDocRef: String): Future[Option[VehicleRegistration]] = {
    if(v5cDocRef.trim.isEmpty)
      db.run(filterQuery(regNumber.trim.toUpperCase, vehicleMake.trim.toUpperCase).result.headOption)
    else
      db.run(filterQuery(regNumber.trim.toUpperCase, vehicleMake.trim.toUpperCase, v5cDocRef.trim.toUpperCase).result.headOption)
  }

  def list(): Future[Seq[VehicleRegistration]] =
    db.run(vehicleRegistrations.result)
}

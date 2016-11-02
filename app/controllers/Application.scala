package controllers

import com.google.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.database.{Client, ClientLogic}
import services.{Pet, PetLogic}

import scala.concurrent.ExecutionContext.Implicits.global

case class RegisterItem(firstName: String, lastName: String, petName: String, petNotes: String)
object RegisterItem {
  implicit val registerFormat = Json.format[RegisterItem]
}

class Application @Inject()(clientsLogic: ClientLogic, petsLogic: PetLogic) extends Controller {
  def index = Action { implicit request =>
    Ok(views.html.index("Sofia"))
  }

  def registerNewPet = Action.async { request =>

    val json: JsValue = request.body.asJson.get
    val registerItem = json.as[RegisterItem]

    for {
      petId <- petsLogic.addPet(Pet(registerItem.petName, registerItem.petNotes))
      result <- clientsLogic.addClient(Client(registerItem.firstName, registerItem.lastName, petId))
    } yield Ok("Success")
  }

  def getAllClients = Action.async {
    clientsLogic.returnAllClients().map(client => Ok(client.mkString(", ")))
  }

  def getAllPets = Action.async {
    petsLogic.returnAllPets().map(pet => Ok(pet.mkString(", ")))
  }
}
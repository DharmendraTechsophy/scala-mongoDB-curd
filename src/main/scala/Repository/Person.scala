package Repository
import reactivemongo.api.bson.collection.BSONCollection
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.{AsyncDriver, Cursor, DB, MongoConnection}
import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros, document}

import scala.util.{Failure, Success}

object GetStarted {
  // My settings (see available connection options)
  val mongoUri = "mongodb://127.0.0.1:27017/"

  import ExecutionContext.Implicits.global // use any appropriate context

  // Connect to the database: Must be done only once per application
  val driver = AsyncDriver()
  val parsedUri = MongoConnection.fromString(mongoUri)

  // Database and collections: Get references
  val futureConnection = parsedUri.flatMap(driver.connect(_))
  def db1: Future[DB] = futureConnection.flatMap(_.database("mydb"))
  def personCollection: Future[BSONCollection] = db1.map(_.collection("person"))


  // Write Documents: insert or update

  implicit def personWriter: BSONDocumentWriter[Person] = Macros.writer[Person]
  // or provide a custom one

  // use personWriter
  def createPerson(person: Person): Future[Unit] =
    personCollection.flatMap(_.insert.one(person).map(_ => {}))

  def updatePerson(person: Person): Future[Int] = {
    val selector = document(
      "firstName" -> person.firstName,
      "lastName" -> person.lastName
    )
    // Update the matching person
    personCollection.flatMap(_.update.one(selector, person).map(_.n))
  }

  implicit def personReader: BSONDocumentReader[Person] = Macros.reader[Person]
  // or provide a custom one

  def findPersonByAge(age: Int): Future[List[Person]] =
    personCollection.flatMap(_.find(document("age" -> age)). // query builder
      cursor[Person](). // using the result cursor
      collect[List](-1, Cursor.FailOnError[List[Person]]()))
  // ... deserializes the document using personReader

  def getAll(): Future[List[Person]] =
    personCollection.flatMap(_.find(document()). // query builder
      cursor[Person](). // using the result cursor
      collect[List](10, Cursor.FailOnError[List[Person]]()))

  def delete(person:Person) = {
    val selector1 = BSONDocument("firstName" -> person.firstName)
    val futureRemove1 = personCollection.flatMap(_.delete.one(selector1))
    futureRemove1.onComplete { // callback
      case Failure(e) => throw e
      case Success(writeResult) => println("successfully removed document")
    }
  }

  // Custom persistent types
  case class Person(firstName: String, lastName: String, age: Int)


  def main(args: Array[String])
  {
    //createPerson(Person("Sorya","Kumar",41)) // working
    //updatePerson(Person("john","Snow", 26));//working
    println(findPersonByAge(22))
    //getAll()
    delete(Person("ADitya","rAnjan",41))//working
  }

}

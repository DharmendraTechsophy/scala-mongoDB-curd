package Repository
import reactivemongo.api.bson.collection.BSONCollection
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.{AsyncDriver, Cursor, DB, MongoConnection}
import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros, document}

import scala.util.{Failure, Success}

object UniversityRepo {

  val mongoUri = "mongodb://127.0.0.1:27017/"

  import ExecutionContext.Implicits.global

  val driver = AsyncDriver()
  val parsedUri = MongoConnection.fromString(mongoUri)


  val futureConnection = parsedUri.flatMap(driver.connect(_))
  def db1: Future[DB] = futureConnection.flatMap(_.database("mydb"))
  def universityCollection: Future[BSONCollection] = db1.map(_.collection("university"))

  implicit def universityWriter: BSONDocumentWriter[University] = Macros.writer[University]

  def create(university: University): Future[Unit] =
    universityCollection.flatMap(_.insert.one(university).map(_ => {}))

  def update(person: University): Future[Int] = {
    val selector = document(
      "id" -> person.id
    )

    universityCollection.flatMap(_.update.one(selector, person).map(_.n))
  }

  implicit def personReader: BSONDocumentReader[University] = Macros.reader[University]


  def findByID(id: Int): Future[List[University]] =
    universityCollection.flatMap(_.find(document("id" -> id)). // query builder
      cursor[University]().
      collect[List](1, Cursor.FailOnError[List[University]]()))


  def getAll(): Future[List[University]] =
    universityCollection.flatMap(_.find(document()). // query builder
      cursor[University](). // using the result cursor
      collect[List](10, Cursor.FailOnError[List[University]]()))

  def delete(university:University) = {
    val selector1 = BSONDocument("firstName" -> university.id)
    val futureRemove1 = universityCollection.flatMap(_.delete.one(selector1))
    futureRemove1.onComplete { // callback
      case Failure(e) => throw e
      case Success(writeResult) => println("successfully removed document")
    }
  }

  case class University(name: String, location: String, id:Option[Int])

//
//  def main(args: Array[String])
//  {
//    create(University("HCU","Hyderabad",Some(1)))
//    create(University("BHU","Banaras",Some(2)))
//    create(University("DU","Delhi",Some(3)))
//  }

}

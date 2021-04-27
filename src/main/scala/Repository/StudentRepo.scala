package Repository

import reactivemongo.api.bson.collection.BSONCollection
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.{AsyncDriver, Cursor, DB, MongoConnection}
import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros, document}

import scala.util.{Failure, Success}

class StudentRepo {

  val mongoUri = "mongodb://127.0.0.1:27017/"

  import ExecutionContext.Implicits.global

  val driver = AsyncDriver()
  val parsedUri = MongoConnection.fromString(mongoUri)

  // Database and collections: Get references
  val futureConnection = parsedUri.flatMap(driver.connect(_))
  def db1: Future[DB] = futureConnection.flatMap(_.database("mydb"))
  def studentCollection: Future[BSONCollection] = db1.map(_.collection("student"))
  implicit def studentWriter: BSONDocumentWriter[Student] = Macros.writer[Student]



  def create(student: Student): Future[Unit] =
    studentCollection.flatMap(_.insert.one(student).map(_ => {}))

  def update(person: Student): Future[Int] = {
    val selector = document("id" -> person.id)
    studentCollection.flatMap(_.update.one(selector, person).map(_.n))
  }

  implicit def personReader: BSONDocumentReader[Student] = Macros.reader[Student]

  def findById(id: Int): Future[List[Student]] =
    studentCollection.flatMap(_.find(document("id" -> id)).
      cursor[Student]().
      collect[List](1, Cursor.FailOnError[List[Student]]()))


  def getAll(): Future[List[Student]] =
    studentCollection.flatMap(_.find(document()).
      cursor[Student]().
      collect[List](10, Cursor.FailOnError[List[Student]]()))

  def delete(student:Student) = {
    val selector1 = BSONDocument("id" -> student.id)
    val futureRemove1 = studentCollection.flatMap(_.delete.one(selector1))
    futureRemove1.onComplete {
      case Failure(e) => throw e
      case Success(writeResult) => println("successfully removed document")
    }
  }
  case class Student(name: String, email: String, uId: Int, id: Option[Int])


//  def main(args: Array[String])
//  {
//    create(Student("John","j@gmail.com",1,Some(2)))
//    create(Student("sorya","sor@gmail.com",3,Some(3)))
//    create(Student("Aditya","adi@gmail.com",2,Some(4)))
//  }

}

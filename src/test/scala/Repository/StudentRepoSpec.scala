package Repository

import org.scalatest._
import funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfter
import com.github.simplyscala.MongodProps

class StudentRepoSpec extends AnyFunSuite with StudentRepo with BeforeAndAfter{

  //declares a variable which will hold the reference to running mongoDB Instance
  var mongoInstance: MongodProps = null
  // Start In-memory Mongo instance in before statement
  before {
    try{ mongoInstance = mongoStart(27017) } //Try starting mongo on this default port
    catch { case ex:Exception => } // Handle exception In case local mongo is running
  }

  //Stop mongo Instance After Running test Case
  after {
    mongoStop(mongoInstance)
  }

  val mongoCRUD = new MongoCRUD
  test("Should be able to insert person Object into MongoDB"){
    val person = MongoDBObject("name"->"Manish")
    val queryResult = mongoCRUD.insertPerson(person)
    //assert if the document was inserted into database
    println(mongoCRUD.findPerson(person).toList)
    assert(mongoCRUD.findPerson(person).count === 1)
  }
}
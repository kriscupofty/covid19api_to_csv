package assignment


import java.io.{BufferedReader, FileReader}
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterEach, FlatSpec, GivenWhenThen}

import scala.collection.mutable.ListBuffer
import scala.reflect.io.File

@RunWith(classOf[org.scalatest.junit.JUnitRunner])
class MainSpec extends FlatSpec with BeforeAndAfterEach with GivenWhenThen {

  val fileName = "COVID19_data.csv"

  override def beforeEach = {
    File(fileName).delete()
  }

  behavior of "Main.main"

  it should "produce the right CSV file given no arguments" in {
    When("there are no arguments")
    val arguments = Array[String]()
    Main.main(arguments)

    Then("a CSV file should be produced")
    assert(File(fileName).exists)

    val reader: BufferedReader = new BufferedReader(new FileReader(fileName))
    var line: String = reader.readLine
    var n: Int = 0
    while (line != null) {
      n += 1
      line = reader.readLine
    }
    reader.close()

    And("the file should have entries")
    assert(n > 1)
  }

  it should "produce the right CSV file given two arguments" in {
    When("there are two arguments")
    val arguments = Array[String]("2020-06-20T22:19:30Z", "2020-06-22T22:19:30Z")
    Main.main(arguments)

    Then("a CSV file should be produced")
    assert(File(fileName).exists)

    val reader: BufferedReader = new BufferedReader(new FileReader(fileName))
    var line: String = reader.readLine
    var n: Int = 0
    val provActual = ListBuffer[String]()
    val datesActual = ListBuffer[String]()
    while (line != null) {
      n += 1
      provActual += line.split(",")(1)
      datesActual += line.split(",")(0)
      line = reader.readLine
    }
    reader.close()

    And("the file should have 29 entries")
    assert(n == 29)

    And("the entries should be sorted according to the province")
    val provExpected = List[String]("Province", "", "", "Alberta", "Alberta", "British Columbia", "British Columbia",
      "Grand Princess", "Grand Princess", "Manitoba", "Manitoba", "New Brunswick", "New Brunswick", "Newfoundland and Labrador",
      "Newfoundland and Labrador", "Northwest Territories", "Northwest Territories", "Nova Scotia", "Nova Scotia", "Ontario",
      "Ontario", "Prince Edward Island", "Prince Edward Island", "Quebec", "Quebec", "Saskatchewan", "Saskatchewan",
      "Yukon", "Yukon")
    assert(provActual.toList.equals(provExpected))

    And("the entries should be sorted according to the date")
    assert(datesActual(1) == "2020-06-21T00:00:00Z" && datesActual(2) == "2020-06-22T00:00:00Z")
  }

}
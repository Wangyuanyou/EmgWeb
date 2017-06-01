package controllers

import javax.inject._

import models.Job
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
//import anorm._
import anorm._

@Singleton
class HomeController @Inject() extends Controller {

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def login = Action{
    Ok(views.html.login("用户登录"))
  }

  def doLogin = Action { implicit request =>
    val loginForm = Form(
      tuple(
        "userName" -> email,
        "password" -> text
      )
    )

    loginForm.bindFromRequest().fold(
      errorForm => Ok(errorForm.errors.toString()),
      tupleData => {
        val (userName, password) = tupleData
        Ok(userName + "&" + password)
      }
    )
  }
  def showJobs= Action{
    var show="the job is :"
    val ds = DB.getDataSource("default")
    val connection = DB.getConnection()
    val statement = connection.createStatement()
    val state_str="select * from jobLib where jobname=\"JdbcInputJob\""
    val result=statement.executeQuery(state_str)
    result.next()
    show +=result.getInt("id")+","+result.getString("jobinfo")
    connection.close()
    Ok(show)
  }
  def index =Action{
    Ok(views.html.index())
  }
  def main=Action{
    Ok(views.html.main())
  }
  def test = Action{
    Ok(views.html.test())
  }

  def testselect= Action{
    val jobs= Job.listAllJob()
    Ok(views.html.testselect(jobs))
  }
  def listSelect = Action { implicit request =>
    val selectForm = Form(
      single(
        "select" -> text
      )
    )
    val emailValue = selectForm.data.get("select")
    val str="ok"+emailValue
    Ok(str)
  }


}


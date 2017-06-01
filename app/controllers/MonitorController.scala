package controllers

import java.io.File
import javax.inject.Inject

import play.api.mvc.Controller
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.db._
import models._

/**
  * Created by wangyuanyou on 5/21/17.
  */
class MonitorController  @Inject() extends Controller{
  def listCurrentTask=Action {
    val tasks= Task.listCurrentTask()
    Ok(views.html.currentTaskList(tasks))
  }
  def listHistoryTask()=Action{
    val tasks= Task.listHistoryTask
    Ok(views.html.historyTaskList(tasks))
  }
  /*def stopTask(id:Int)=Action{
    Task.stopOneTask(id)
    val tasks= Task.listHistoryTask
    Ok(views.html.historyTaskList(tasks))
  }*/
}

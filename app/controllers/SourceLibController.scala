package controllers

import java.io.File
import java.util.concurrent.{ExecutorService, Executors}
import javax.inject.Inject

import play.api.mvc.Controller
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.db._
import models._

import scala.sys.process._
import java.lang.Thread

/**
  * Created by wangyuanyou on 5/20/17.
  */
class SourceLibController @Inject() extends Controller {
  var mySparkPool:Map[Int,Thread]=Map()
  var myFlumePool:Map[Int,Thread]=Map()
  def listSource=Action{
    val sources=Source.listAllSource()
    Ok(views.html.sourceManage(sources))
  }
  def deleteSource(id:Int)=Action{
    Source.deleteOneSource(id)
    Redirect("/sourceManage")
  }
  def updateSourceinfo(id:Int,name:String,sourcetype:String)=Action{
    Ok(views.html.updateSource(id,name,sourcetype))
  }
  def uploadConfig=Action(parse.multipartFormData) { request =>
    request.body.file("oneconfig").map { oneConfig =>
      import java.io.File
      val filename = oneConfig.filename
      val contentType = oneConfig.contentType
      oneConfig.ref.moveTo(new File(s"public/uploadJobs/$filename"))
      Redirect("/updatesource")
    }.getOrElse {
      Redirect("/updatesource").flashing(
        "error" -> "Missing file")
    }
  }
  def updateOneSource()= Action { implicit request =>
    val sourceupdateForm = Form(
      tuple(
        "id" -> number,
        "name" -> text,
        "sourcetype" -> text
      )
    )
    sourceupdateForm.bindFromRequest().fold(
      errorForm => Ok(errorForm.errors.toString()),
      tupleData => {
        val (id, name, sourcetype) = tupleData
        val one = new Source(id, name, sourcetype, "")
        Source.updateOneSource(one)
        Redirect("/sourceManage")
      }
    )
  }
  def inputNewSourceInfo=Action{
    Ok(views.html.inputNewSource())
  }
  def addSource=Action { implicit request =>
    val SourceInfoForm = Form(
      tuple(
        "id" -> number,
        "name" -> text,
        "sourcetype" -> text
      )
    )
    SourceInfoForm.bindFromRequest().fold(
      errorForm => Ok(errorForm.errors.toString()),
      tupleData => {
        val (id, name, sourcetype) = tupleData
        val one = new Source(id, name, sourcetype, "")
        Source.addOneSource(one)
        Redirect("/sourceManage")
      }
    )
  }
  def startIncrease(id:Int)=Action{
    val taskID=Task.startNewTask(id,1)

    if (id==2) {
      val threadSpark = new Thread(new runSparkGoods)
      val threadFlume = new Thread(new runFlumeGoods)
      mySparkPool += (taskID -> threadSpark)
      myFlumePool += (taskID -> threadFlume)
      threadSpark.start()
      threadFlume.start()
    }
    else {
      val threadSpark = new Thread(new runSparkLog)
      val threadFlume = new Thread(new runFlumeLog)
      mySparkPool += (taskID -> threadSpark)
      myFlumePool += (taskID -> threadFlume)
      threadSpark.start()
      threadFlume.start()
    }
    Redirect("/listCurrentTask ")
  }
  def startFull(id:Int)=Action{
    val taskID=Task.startNewTask(id,0)
    val threadSpark = new Thread(new runGoodsFull)
    mySparkPool += (taskID -> threadSpark)
    threadSpark.start()
    Redirect("/listCurrentTask ")
  }
  def stopTask(id:Int)=Action{
    Task.stopOneTask(id)
    //mySparkPool(id).stop()
    //myFlumePool(id).stop()
    val tasks= Task.listHistoryTask
    Ok(views.html.historyTaskList(tasks))
  }

  def lookupHistory(id:Int)=Action{
   val historyTasks=Task.listHistoryTaskBySourceID(id:Int)
    Ok(views.html.historyTaskList(historyTasks))
  }
// runable classes
  class runSparkGoods()extends Runnable{
    override def run(){
      "spark-submit --class com.emg.spark.test.TestFlume --master spark://127.0.0.1:7077 /home/wangyuanyou/Documents/Documents/Spark/SparkAPP/emergency.jar" !
    }
  }

  class runFlumeGoods()extends Runnable{
    override def run(){
      "flume-ng agent -n agent1 -c conf -f /usr/local/flume/apache-flume-1.6.0-bin/conf/flume-conf.properties" !
    }
  }
  class runSparkLog()extends Runnable{
    override def run(){
      "spark-submit --class com.emg.spark.test.TestLog --master spark://127.0.0.1:7077 /home/wangyuanyou/Documents/Documents/Spark/SparkAPP/emergency.jar" !
    }
  }

  class runFlumeLog()extends Runnable{
    override def run(){
      "flume-ng agent -n agent2 -c conf -f /usr/local/flume/apache-flume-1.6.0-bin/conf/flume-conf_agent2.properties" !
    }
  }
  class runGoodsFull()extends Runnable{
    override def run(){
      "spark-submit --class com.emg.spark.test.TryJson --master spark://127.0.0.1:7077 --driver-class-path /usr/local/spark/spark-1.5.1-bin-hadoop2.6/lib/mysql-connector-java-5.1.42-bin.jar /home/wangyuanyou/Documents/Documents/Spark/SparkAPP/emergency.jar" !
    }
  }

  // runable classes

}

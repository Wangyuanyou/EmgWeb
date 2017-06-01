package models

import java.text.SimpleDateFormat
import java.util.Date

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import org.joda.time.{DateTime, Duration}

import scala.io.Source

/**
  * Created by wangyuanyou on 5/23/17.
  */
case class TaskDetail(id:Int,taskid:Int,time:String,amount:Int)
object TaskDetail {
  val simple = {
    get[Int]("id") ~
      get[Int]("taskid") ~
      get[String]("time") ~
      get[Int]("amount") map {
      case id ~ taskid ~ time ~ amount => TaskDetail(id, taskid, time, amount)
    }
  }
  def getDetailsByID(id:Int):Seq[TaskDetail]={
    val sql_str="select * from task_detail where taskid="+id
    DB.withConnection { implicit connection =>
      SQL(sql_str).as(TaskDetail.simple *)
    }
  }
  def getAmountByID(id:Int):Int={
    val fileName = "/home/wangyuanyou/Documents/Documents/Spark/sparkRuning/detailOfIncreae"
    val source = scala.io.Source.fromFile(fileName)
    var amount=0
    for (line <- source.getLines)
      amount=amount+line.toInt
    return amount
    //val details=TaskDetail.getDetailsByID(id)
    //var amount=0
    //for(detail <- details){
      //amount=amount+detail.amount
    //}
    //return amount
  }
  def getSpeedByID(id:Int):Double={
    val amount=TaskDetail.getAmountByID(id)

    val currentTask=Task.getTaskByID(id)(0)
    val start_str=currentTask.time_start
    val format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val start_day=format.parse(start_str)
    val start_daytime=new DateTime(start_day)

    val now = new Date()
    val initialNow  = now.getTime
    val ini_str = initialNow+""
    val timetamp=ini_str.substring(0,10).toLong
    val sdf:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val end_str:String = sdf.format(new Date((timetamp.toLong*1000l)))
    val end_day=format.parse(end_str)
    val end_daytime=new DateTime(end_day)

    val during=new Duration(start_daytime,end_daytime)
    val mins=during.getStandardMinutes
    var speed:Double=0
    if(mins==0){
      speed=amount
    }
    else{
      speed=amount/mins
    }
    return speed
  }
}

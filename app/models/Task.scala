package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.text.SimpleDateFormat
import java.util.Date

import org.joda.time._
/**
  * Created by wangyuanyou on 5/21/17.
  */
case class Task(id:Int,sourceid:Int,target:String,tasktype:Int,state:Int,time_start:String,time_end:String,amount:Int,speed:Double)
object Task {
  val simple = {
      get[Int]("id")~
      get[Int]("sourceid")~
      get[String]("target")~
      get[Int]("tasktype")~
      get[Int]("state")~
      get[String]("time_start")~
      get[String]("time_end")~
      get[Int]("amount")~
      get[Double]("speed")map{
      case id~sourceid~target~tasktype~state~time_start~time_end~amount~speed=>Task(id,sourceid,target,tasktype,state,time_start,time_end,amount,speed)
    }
  }
  def listCurrentTask():Seq[Task]= {
    DB.withConnection { implicit connection =>
      SQL("select * from tasks where state=1").as(Task.simple *)
    }
  }
  def listHistoryTask():Seq[Task]={
    DB.withConnection { implicit connection =>
      SQL("select * from tasks where state=0").as(Task.simple *)
    }
  }
  def startNewTask(sourceid:Int,tasktype:Int):Int={
    val now = new Date()
    val initial  = now.getTime
    var ini_str = initial+""
    var timetamp=ini_str.substring(0,10).toLong
    var sdf:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var time:String = sdf.format(new Date((timetamp.toLong*1000l)))

    val maxIDTask=DB.withConnection { implicit connection =>
      SQL("select * from tasks where id = (select max(id) from tasks)").as(Task.simple *)
    }
    val newID=maxIDTask(0).id+1
    var changeTarget=""
    if(tasktype==0)
      changeTarget="table:good;HDFS:/emergency/goods/fullHistory"
    else
      changeTarget="table:good;HDFS:/emergency/goods/increseHistory"
    val newTask=new Task(newID,sourceid,changeTarget,tasktype,
      1,time,"",0,0)
    DB.withConnection { implicit connection =>
      SQL("insert into tasks(id,sourceid,target,tasktype,state,time_start,time_end,amount,speed) values ({id},{sourceid},{target},{tasktype},{state},{time_start},{time_end},{amount},{speed})").
        on('id->newTask.id,'sourceid->newTask.sourceid,'target->newTask.target,'tasktype->newTask.tasktype,'state->newTask.state,'time_start->newTask.time_start,'time_end->newTask.time_end,'amount->newTask.amount,'speed->newTask.speed).executeUpdate()
    }
    return newID
  }
  def listHistoryTaskBySourceID(id:Int):Seq[Task]={
    val sql_str= "select * from tasks where state=0 and sourceid="+id
    DB.withConnection { implicit connection =>
      SQL(sql_str).as(Task.simple *)
    }
  }
  def getTaskByID(id:Int):Seq[Task]={
    val sql_str= "select * from tasks where id="+id
    DB.withConnection { implicit connection =>
      SQL(sql_str).as(Task.simple *)
    }
  }
  def getSpeedByID(id:Int):Double={
    val sql_str= "select * from tasks where id="+id
    val idTask=DB.withConnection { implicit connection =>
      SQL(sql_str).as(Task.simple *)
    }
    if(idTask(0).speed>0)
      return idTask(0).speed
    else
      return idTask(0).amount
  }

  def stopOneTask(id:Int):Unit={
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


    DB.withConnection { implicit connection =>
      SQL("update tasks set state=0,time_end={end_str},amount={amount},speed={speed} where id = {id}").
        on('id->id,'end_str->end_str,'amount->amount,'speed->speed).executeUpdate()
    }
  }
}

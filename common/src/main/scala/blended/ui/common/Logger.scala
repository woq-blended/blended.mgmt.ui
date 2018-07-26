package blended.ui.common

import scala.reflect.{ClassTag, classTag}

trait Logger extends Serializable {
  def error(msg: => String, t: Throwable) : Unit = error(msg, Some(t))
  def warn(msg: => String, t: Throwable) : Unit = warn(msg, Some(t))
  def info(msg: => String, t: Throwable) : Unit = info(msg, Some(t))
  def debug(msg: => String, t: Throwable) : Unit = debug(msg, Some(t))
  def trace(msg: => String, t: Throwable) : Unit = trace(msg, Some(t))

  def error(msg: => String): Unit = error(msg, None)
  def warn(msg: => String) : Unit = warn(msg, None)
  def info(msg: => String) : Unit = info(msg, None)
  def debug(msg: => String) : Unit = debug(msg, None)
  def trace(msg: => String) : Unit = trace(msg, None)

  def error(msg: => String, t: Option[Throwable])
  def warn(msg: => String, t: Option[Throwable])
  def info(msg: => String, t: Option[Throwable])
  def debug(msg: => String, t: Option[Throwable])
  def trace(msg: => String, t: Option[Throwable])
}

class PrintlnLogger(className: String) extends Logger {
  override def error(msg: => String, t: Option[Throwable]) : Unit = log("ERROR", t, msg)
  override def warn(msg: => String, t: Option[Throwable]) : Unit = log("WARN", t, msg)
  override def info(msg: => String, t: Option[Throwable]) : Unit = log("INFO", t, msg)
  override def debug(msg: => String, t: Option[Throwable]) : Unit = log("DEBUG", t, msg)
  override def trace(msg: => String, t: Option[Throwable]) : Unit = log("TRACE", t, msg)

  private[this] def log(level: String, t: Option[Throwable], msg: => String) : Unit = {
    // scalastyle:off regex
    println("[" + level + "] " + className + ": " + msg)
    t.foreach(println)
    // scalastyle:on regex
  }

  override def toString : String = getClass.getSimpleName + "(className=" + className + ")"
}

class NoopLogger extends Logger {
  override def error(msg: => String, t: Option[Throwable]) : Unit = {}
  override def warn(msg: => String, t: Option[Throwable]) : Unit = {}
  override def info(msg: => String, t: Option[Throwable]) : Unit = {}
  override def debug(msg: => String, t: Option[Throwable]) : Unit = {}
  override def trace(msg: => String, t: Option[Throwable]) : Unit = {}
}

object Logger {

  def apply[T: ClassTag]: Logger = {
    apply(classTag[T].runtimeClass.getName)
  }

  def apply(className: String): Logger = {
    // for now, delegate all log message to the print logger
    new PrintlnLogger(className)
  }
}


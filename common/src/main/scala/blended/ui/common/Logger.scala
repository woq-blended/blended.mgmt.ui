package blended.ui.common

import scala.reflect.{ClassTag, classTag}

trait Logger extends Serializable {
  def error(t: Throwable)(msg: => String) : Unit = error(Some(t))(msg)
  def warn(t: Throwable)(msg: => String) : Unit = warn(Some(t))(msg)
  def info(t: Throwable)(msg: => String) : Unit = info(Some(t))(msg)
  def debug(t: Throwable)(msg: => String) : Unit = debug(Some(t))(msg)
  def trace(t: Throwable)(msg: => String) : Unit = trace(Some(t))(msg)

  def error(msg: => String): Unit = error(None)(msg)
  def warn(msg: => String) : Unit = warn(None)(msg)
  def info(msg: => String) : Unit = info(None)(msg)
  def debug(msg: => String) : Unit = debug(None)(msg)
  def trace(msg: => String) : Unit = trace(None)(msg)

  def error(t: Option[Throwable])(msg: => String) : Unit
  def warn(t: Option[Throwable])(msg: => String) : Unit
  def info(t: Option[Throwable])(msg: => String) : Unit
  def debug(t: Option[Throwable])(msg: => String) : Unit
  def trace(t: Option[Throwable])(msg: => String) : Unit
}

class PrintlnLogger(className: String) extends Logger {
  override def error(t: Option[Throwable])(msg: => String) : Unit = log("ERROR", t, msg)
  override def warn(t: Option[Throwable])(msg: => String) : Unit = log("WARN", t, msg)
  override def info(t: Option[Throwable])(msg: => String) : Unit = log("INFO", t, msg)
  override def debug(t: Option[Throwable])(msg: => String) : Unit = log("DEBUG", t, msg)
  override def trace(t: Option[Throwable])(msg: => String) : Unit = log("TRACE", t, msg)

  private[this] def log(level: String, t: Option[Throwable], msg: => String) : Unit = {
    // scalastyle:off regex
    println("[" + level + "] " + className + ": " + msg)
    t.foreach(println)
    // scalastyle:on regex
  }

  override def toString : String = getClass.getSimpleName + "(className=" + className + ")"
}

class NoopLogger extends Logger {
  override def error(t: Option[Throwable])(msg: => String) : Unit = {}
  override def warn(t: Option[Throwable])(msg: => String) : Unit = {}
  override def info(t: Option[Throwable])(msg: => String) : Unit = {}
  override def debug(t: Option[Throwable])(msg: => String) : Unit = {}
  override def trace(t: Option[Throwable])(msg: => String) : Unit = {}
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


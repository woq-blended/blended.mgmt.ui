package blended.ui.samples.state

import blended.jmx.JmxObjectName
import blended.ui.samples.{HomePage, SamplePage}

case class Person(
  first: String,
  last: String,
  age: Int,
  eMail: String
)

sealed trait SampleAppEvent
final case class PageSelected(p: Option[SamplePage]) extends SampleAppEvent

object SampleAppState {

  def redux(event: SampleAppEvent)(old: SampleAppState) : SampleAppState = event match {
    case PageSelected(p) =>
      old.copy(currentPage = p)
  }
}

case class SampleAppState(

  currentPage : Option[SamplePage] = Some(HomePage),

  persons : Seq[Person] = Seq(
    // scalastyle:off magic.number
    Person("Andreas", "Gies", 50, "andreas@wayofquality.de"),
    Person("Karin", "Gies", 52, "kgies@godea-life.de"),
    Person("Tatjana", "Gies", 28, "gies_tat@yahoo.com"),
    Person("Sabrina", "Gies", 24, "sabrina@godea-life.de")
    // scalastyle:on magic.number
  ),

  names : List[JmxObjectName] = List(
    JmxObjectName("java.lang:name=Metaspace,type=MemoryPool").get,
    JmxObjectName("java.lang:name=PS Old Gen,type=MemoryPool").get,
    JmxObjectName("java.lang:name=PS Scavenge,type=GarbageCollector").get,
    JmxObjectName("java.lang:name=PS Eden Space,type=MemoryPool").get,
    JmxObjectName("JMImplementation:type=MBeanServerDelegate").get,
    JmxObjectName("java.lang:type=Runtime").get,
    JmxObjectName("java.lang:type=Threading").get,
    JmxObjectName("java.lang:type=OperatingSystem").get,
    JmxObjectName("java.lang:name=Code Cache,type=MemoryPool").get,
    JmxObjectName("java.nio:name=direct,type=BufferPool").get,
    JmxObjectName("java.lang:type=Compilation").get,
    JmxObjectName("java.lang:name=CodeCacheManager,type=MemoryManager").get,
    JmxObjectName("java.lang:name=Compressed Class Space,type=MemoryPool").get,
    JmxObjectName("java.lang:type=Memory").get,
    JmxObjectName("java.nio:name=mapped,type=BufferPool").get,
    JmxObjectName("java.util.logging:type=Logging").get,
    JmxObjectName("java.lang:name=PS Survivor Space,type=MemoryPool").get,
    JmxObjectName("java.lang:type=ClassLoading").get,
    JmxObjectName("java.lang:name=Metaspace Manager,type=MemoryManager").get,
    JmxObjectName("com.sun.management:type=DiagnosticCommand").get,
    JmxObjectName("java.lang:name=PS MarkSweep,type=GarbageCollector").get,
    JmxObjectName("com.sun.management:type=HotSpotDiagnostic").get,
    JmxObjectName("jdk.management.jfr:type=FlightRecorder").get
  )
)


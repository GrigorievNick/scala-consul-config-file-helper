import java.io.File
import java.net.URL

import spray.json._

import scala.io.Source.fromInputStream


object App {

  case class ServiceContainer(service: Service)

  case class Service(id:String, name: String, tags: Array[String], address: String, port: Int, enableTagOverride: Boolean, check: Check)

  case class Check(id: String, name: String, http: String, interval: String, timeout: String)

  case class ServiceNode(ModifyIndex: Int, ServiceName: String, ServiceAddress: String, Address: String, Node: String,
                         ServicePort: Int, ServiceEnableTagOverride: Boolean, ServiceTags: Array[String],
                         ServiceID: String, CreateIndex: Int)

  case class ServiceNodeList(serviceNodes: List[ServiceNode])

  object ConsulProtocol extends DefaultJsonProtocol {
    implicit val checkFormat = jsonFormat5(Check)
    implicit val serviceFormat = jsonFormat7(Service)
    implicit val serviceContainerFormat = jsonFormat1(ServiceContainer)
    implicit val serviceNodeFormat = jsonFormat10(ServiceNode)
  }

  def main(args: Array[String]) {
    val dataDir: String = "data-dir/"
    new File(dataDir).mkdir()
    import ConsulProtocol._

    val url: URL = new URL("http://10.2.4.249:8500/v1/catalog/service/edc")
    val con = url.openConnection()
    val arrServiceNode = fromInputStream(con.getInputStream).mkString.parseJson
    val serviceNodeList: Array[ServiceNode] = arrServiceNode.convertTo[Array[ServiceNode]]
    serviceNodeList.foreach(node => {
      val serviceName: String = node.ServiceTags(0).replaceAll("name=", "").replaceAll("\\s*", "")
      printToFile(new java.io.File(dataDir + serviceName + ".json")
      ) { p => p.println(
        ServiceContainer(
          Service(node.ServiceName + "-" + serviceName, node.ServiceName, node.ServiceTags, node.Address, node.ServicePort, node.ServiceEnableTagOverride,
            Check("api", "APi health check " + node.ServicePort, "http://" + node.Address + ":" + node.ServicePort + "/health", "30s", "10s"))
        ).toJson.prettyPrint
      )
      }
    })
  }

  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }


}

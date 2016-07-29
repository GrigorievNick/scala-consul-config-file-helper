/**
  * Created by mhr on 7/28/16.
  */
case class Service(name: String, tags: Array[String], address: String, port: Int, enableTagOverride: Boolean, check: Check)

case class Check(id: String, name: String, http: String, interval: String, timeout: String)

case class ServiceNode(
                        ModifyIndex: String, ServiceName: String, ServiceAddress: String, Address: String, Node: String,
                        ServicePort: String, ServiceEnableTagOverride: Boolean, ServiceTags: Array[String],
                        ServiceID: String, CreateIndex: Int)

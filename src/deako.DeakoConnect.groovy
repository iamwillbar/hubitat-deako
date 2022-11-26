metadata {
  definition (name: "Deako Connect", namespace: "deako", author: "William Bartholomew") {
    capability "Initialize"
  }
}

preferences {
  input "ip", "text", title: "Deako Connect IP Address", description: "", required: true, displayDuringSetup: true
  input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
}

def installed() {
  log.debug "Installed"
}

def updated() {
  log.debug "Updated"
  unschedule()
  runIn(5, initialize)
}

def uninstalled() {
  telnetClose()
  removeChildDevices()
}

def initialize() {
  log.debug "Initializing"
  telnetClose()
  connectToDeako()
  sendDeako("{\"transactionId\": \"${UUID.randomUUID().toString()}\", \"type\": \"DEVICE_LIST\", \"dst\": \"deako\", \"src\": \"Hubitat\"}")
}

def connectToDeako() {
  telnetConnect([termChars:[13,10]], settings.ip, 23, null, null)
}

def sendDeako(message) {
  logDebug "sending: ${message}"
  sendHubCommand(new hubitat.device.HubAction(message, hubitat.device.Protocol.TELNET))
}

def parse(message) {
  def json = parseJson(message)
  logDebug "Telnet message: ${json}"
  if (json.type == "DEVICE_FOUND") {
    name = json.data.name
    addChildDevice("deako", "Deako Light", json.data.uuid, [isComponent: false, name: "Deako Light", label: name])
  } else if (json.type == "EVENT" && json.data.eventType == "DEVICE_STATE_CHANGE") {
    deviceNetworkId = json.data.target
    power = json.data.state.power
    dim = json.data.state.dim
    getChildDevice(deviceNetworkId).sendEvent([name: "switch", value: power ? "on" : "off"])
  }
}

def telnetStatus(message) {
  log.error "Status: ${message}"
  initialize()
}

def logDebug(message) {
  if (settings?.logEnable) {
    log.debug message
  }
}

private def removeChildDevices() {
  getChildDevices().each {
    log.warn "Deleting ${it}"
    deleteChildDevice(it.deviceNetworkId)
  }
}

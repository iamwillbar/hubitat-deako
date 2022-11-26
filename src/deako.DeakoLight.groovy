metadata {
  definition (name: "Deako Light", namespace: "deako", author: "William Bartholomew") {
  }
  capability "Bulb"
  capability "Light"
  capability "Switch"
}

def on() {
  getParent().sendDeako("{\"transactionId\": \"${UUID.randomUUID().toString()}\", \"type\": \"CONTROL\", \"dst\": \"deako\", \"src\": \"Hubitat\", \"data\": {\"target\": \"${device.deviceNetworkId}\", \"state\": {\"power\": true, \"dim\": 100}}}")
}

def off() {
  getParent().sendDeako("{\"transactionId\": \"${UUID.randomUUID().toString()}\", \"type\": \"CONTROL\", \"dst\": \"deako\", \"src\": \"Hubitat\", \"data\": {\"target\": \"${device.deviceNetworkId}\", \"state\": {\"power\": false, \"dim\": 100}}}")
}

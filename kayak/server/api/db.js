var Mongolian = require("mongolian")

// Create a server instance with default host and port
isOnline = false
var server = new Mongolian(isOnline ? "admin_kanot:Arsdrdrlksndqlsndqphrfozeish@92.222.89.79:27017": "127.0.0.1:27017")

// Get database
var db = server.db("kanotDB")
module.exports = db
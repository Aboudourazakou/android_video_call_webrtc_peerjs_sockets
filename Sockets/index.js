var app = require("express")()
var http = require("http").Server(app)
var io = require("socket.io")(http, {'pingInterval': 200, 'pingTimeout': 500}
   
)



io.on('connection', (socket) => {

socket.on("call",(data)=>{
       
        socket.broadcast.emit("incoming_call",data)
})
socket.on("reject_call",(data)=>{
    socket.broadcast.emit("reject_call",data)
})
 socket.on("accept_call",(id)=>{
socket.broadcast.emit("accept_call",id)
})

})



var port = process.env.PORT || 4000
http.listen(port, () => {
    console.log("Serveur tourne sur " + port)
})
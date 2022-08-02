let localVideo = document.getElementById("local-video")
let remoteVideo = document.getElementById("remote-video")

localVideo.style.opacity = 0
remoteVideo.style.opacity = 0

localVideo.onplaying = () => { localVideo.style.opacity = 1 }
remoteVideo.onplaying = () => { remoteVideo.style.opacity = 1 }

let peer
function init(userId) {
    peer = new Peer(userId, {
        host: '192.168.1.15',
        port: 9000,
        path: '/videocallapp'
    })

    peer.on('open', () => {
        Android.onPeerConnected()
    })

    listen()
     console.log("Changement de stream frere dehors")
}

let localStream
function listen() {
    peer.on('call', (call) => {

        navigator.getUserMedia({
            audio: true, 
            video: true
        }, (stream) => {
            localVideo.srcObject = stream
            localStream = stream

            call.answer(stream)
            call.on('stream', (remoteStream) => {
               if(remoteStream!=null){
                 remoteVideo.srcObject = remoteStream

                        remoteVideo.className = "primary-video"
                        localVideo.className = "secondary-video"
                        console.log("Changement de stream frere")
               }
               else{
               console.log("Remotre stream is nullard")
               }

            })

        })
        
    })
}

function startCall(otherUserId) {
   try{
        navigator.getUserMedia({
              audio: true,
              video: true
          }, (stream) => {

              localVideo.srcObject = stream
              localStream = stream

              const call = peer.call(otherUserId, stream)
              call.on('stream', (remoteStream) => {
                   remoteVideo.srcObject = remoteStream

                  remoteVideo.className = "primary-video"
                  localVideo.className = "secondary-video"
              })

          })
   }catch(error){
   console.log("Error occured bro" +error)}
}

function toggleVideo(b) {
    if (b == "true") {
        localStream.getVideoTracks()[0].enabled = true
    } else {
        localStream.getVideoTracks()[0].enabled = false
    }
} 

function toggleAudio(b) {
    if (b == "true") {
        localStream.getAudioTracks()[0].enabled = true
    } else {
        localStream.getAudioTracks()[0].enabled = false
    }
} 
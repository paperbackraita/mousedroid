"""
PC Client for MouseDroid.
Set DisablePlugins = pnat in /etc/bluetooth/main.conf if IOError and restart bluetooth.
Author - Sukrit Kalra (Sukrit12108 <at> iiitd.ac.in)
"""

import bluetooth as bt

# Start the server.
server_socket = bt.BluetoothSocket(bt.RFCOMM)
server_socket.bind(("", bt.PORT_ANY))
server_socket.listen(1)

# Get channel used for the communication.
channel = server_socket.getsockname()[1]

# Advertise the service to the SDP server.
uuid = "3DD7E793-C461-4FAE-B715-12E8940A0975"
bt.advertise_service(
		server_socket,
		"mousedroid",
		service_id = uuid,
		)

# Wait for the connection.
print "Waiting for a connection from the Android Client on channel {}".format(channel)

# Accept a connection.
client, address = server_socket.accept()
print "Accepted connection from", address

# Get the data.
data = client.recv(1024)
print "Received [%s]" % data

package network

import groovy.util.CliBuilder
// host = '108.171.177.136'
// host = 'localhost'

def cli = new CliBuilder(usage:'portCheck')
cli.host('Hostname or IP', args: 1, required: true)
cli.from('Port starting number', args: 1, required: true)
cli.to('Port ending number', args: 1, required: true)
cli.timeout('Max timeout shift to', args: 1, required: false)
def options = cli.parse(args)

if (!options) {
  return
}

if (options.h) {
	cli.usage()
   return
}

println "${options.host}:${options.from}..${options.to} < ${options.timeout}"
           
host = options.host
from = options.from as Integer
to = options.to as int
timeout = 2000
maxTimeout = options.timeout ?: 60000

for (port in from..to) {
    try {
        println "Check on $host:$port"
        //s = new Socket(host, port)
        Socket socket = new Socket();
		socket.connect(new InetSocketAddress(host, port as int), timeout);
        println("  ***** Port open $host:$port *****")
    }
    catch (SocketTimeoutException ex) {
        if (timeout < maxTimeout) {
    	   println "   ### ${ex.message} --- timeout is now ${timeout} "
    	   timeout += 500
    	}
	}
	catch (ConnectException) {
	}
}
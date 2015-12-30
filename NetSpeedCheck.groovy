#!/usr/bin/env groovy

/** 
  A simple client or server script that will report on the network speed between the 2 nodes.  
  Really the results are only indicative and should be taken as a rough guess as there are other factors at play between the clients and server but 
  the aim is to give that rough guide and also serves as an examples in Groovy of client/server, simple Threading, gathering command line args and other Groovy'isms
*/

import groovy.time.TimeCategory
import groovy.time.TimeDuration
import util.*

RunOpts runOpts = new RunOpts(args)

println "Running with: ${runOpts}"

if (runOpts.startAsServer) {
    startServer(runOpts)
} else {
    startClient(runOpts)
}

def startServer(RunOpts runOpts) {
    // Setting a default port number.
    int portNumber = 9991

    try {
        // initializing the Socket Server
        SocketServer socketServer = new SocketServer(port: portNumber, runOpts: runOpts)
        socketServer.start()

    } catch (IOException e) {
        e.printStackTrace()
    }
}


def startClient(RunOpts runOpts) {
    SocketClient client = new SocketClient(runOpts)
    try {
        //trying to establish connection to the server
        client.connect()
        //send sometihng to the server
        client.sendData()
        //waiting to read response from server
        client.readResponse()

    } catch (UnknownHostException e) {
        System.err.println("Host unknown. Cannot establish connection")
    } catch (IOException e) {
        System.err.println("Cannot establish connection. Server may not be up." + e.getMessage())
    }
}

/**
 * A simple socket server
 * http://syntx.io/a-client-server-application-using-socket-programming-in-java-part-2/
 *
 */
public class SocketServer {

    private ServerSocket serverSocket
    private int port
    RunOpts runOpts

    public void start() throws IOException {
        println("Starting the socket server at port:" + port)
        serverSocket = new ServerSocket(port)

        Socket client = null

        while (true) {
            println("Waiting for clients...")
            client = serverSocket.accept()
            println("The following client has connected:" + client.getInetAddress().getCanonicalHostName())
            //A client has connected to this server. Send welcome message
            Thread thread = new Thread(new SocketClientHandler(client: client, runOpts: runOpts))
            thread.start()
        }
    }

}

public class SocketClientHandler implements Runnable {

    Socket client

    RunOpts runOpts
    long totalAmountSent = 0
    long totalTimeTaken = 0

    @Override
    public void run() {
        try {
            println("Thread started with name:" + Thread.currentThread().getName())

            receiveData()

            sendData("Bye...")
            /*Date now = new Date()
            int lengthToSend = runOpts.dataBlockSize
            int nrSends = runOpts.numberOfTimesToSend
            String sendMe = ""

            for (int i = 0; i < nrSends; i++) {

                UUID uuid = UUID.randomUUID()
                while (sendMe.length() < lengthToSend) {
                    sendMe += uuid.toString()
                }

                def duration = GroovyUtils.withTiming {->
                    sendData(sendMe)
                }
                totalAmountSent += lengthToSend
                totalTimeTaken += duration

                TimeDuration timeDuration = TimeCategory.minus(new Date(now.time + duration), now)
                println " Sent ${GroovyUtils.humanReadableByteCount(sendMe.length(), false)} in ${timeDuration} secs, send ${i+1}/${nrSends}"
            }

            println "Totals:: "
            TimeDuration timeDuration = TimeCategory.minus(new Date(now.time + totalTimeTaken), now)
            println " Sent ${GroovyUtils.humanReadableByteCount(totalAmountSent, false)} in ${timeDuration} secs"*/
        } catch (IOException e) {
            e.printStackTrace()
        } catch (InterruptedException e) {
            e.printStackTrace()
        }
    }

    /* private void readResponse() throws IOException, InterruptedException {
         String userInput
         BufferedReader stdIn = new BufferedReader(new InputStreamReader(client.getInputStream()))
         while ((userInput = stdIn.readLine()) != null) {
             if (userInput.equals("TIME?")) {
                 println("REQUEST TO SEND TIME RECEIVED. SENDING CURRENT TIME")

                 break
             }
             println(userInput)
         }
     }*/

    private void receiveData() throws IOException, InterruptedException {
        /* BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))

         String userInput
         while ((userInput = reader.readLine()) != null) {
             println(userInput)
         }*/
        String responseLine
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(client.getInputStream()))

        Date now = new Date()
        int byteCount = 0
        def duration = GroovyUtils.withTiming {->
            while ((responseLine = stdIn.readLine()) != null) {
                //println "Received size: ${responseLine.length()}"
                byteCount += responseLine.length()
            }
        }
        TimeDuration timeDuration = TimeCategory.minus(new Date(now.time + duration), now)
        println " Received ${GroovyUtils.humanReadableByteCount(byteCount, false)} in ${timeDuration} secs"
        print("Received ${byteCount} bytes")
    }

    private void sendData(String sendMe) throws IOException, InterruptedException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
        writer.write(sendMe)
        writer.flush()
        writer.close()
    }

}

public class SocketClient {

    private String hostname
    private int port
    Socket socketClient

    RunOpts runOpts
    long totalAmountSent = 0
    long totalTimeTaken = 0

    public SocketClient(RunOpts runOpts) {
        this.hostname = runOpts.hostName
        this.port = Integer.parseInt(runOpts.port)
        this.runOpts = runOpts
    }

    public void connect() throws UnknownHostException, IOException {
        println("Attempting to connect to " + hostname + ":" + port)
        socketClient = new Socket(hostname, port)
        println("Connection Established")
    }

    public void readResponse() throws IOException {
        String responseLine
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()))

        int byteCount = 0
        while ((responseLine = stdIn.readLine()) != null) {
            //println(responseLine)
            byteCount += responseLine.length()
        }
        print("Received ${byteCount} bytes")
    }

    def generator = { String alphabet, int n ->
        new Random().with {
            (1..n).collect { alphabet[nextInt(alphabet.length())] }.join()
        }
    }

    def sendData() {
        Date now = new Date()
        int lengthToSend = runOpts.dataBlockSize
        int totalNrSends = runOpts.numberOfTimesToSend
        int sizePerSend = runOpts.sizePerSend
        String sendMe = ""

        for (int i = 0; i < totalNrSends; i++) {
            int totalSent = 0
            println "Starting send # ${i + 1}"
            UUID uuid = UUID.randomUUID()

            sendMe = generator((('A'..'Z') + ('0'..'9')).join(), sizePerSend)
            /*while (sendMe.length() < lengthToSend) {
                sendMe += uuid.toString()
            }*/
            int maxNrReports = 10
            int reportingFreq = Math.max (((lengthToSend / sizePerSend) * (1/maxNrReports) as int), Math.min((lengthToSend / sizePerSend) as int, maxNrReports))
            int nrSends = 0

            //println "lengthToSend: ${lengthToSend}, sizePerSend: ${sizePerSend} = ${(lengthToSend / sizePerSend)} reporting freq: ${reportingFreq}"
            def duration = GroovyUtils.withTiming {->
                while (totalSent < lengthToSend) {
                    transmit(sendMe)
                    nrSends++
                    totalSent += sizePerSend

                    if (nrSends % reportingFreq == 0) {
                        println "Sent: ${totalSent}"
                    }
                }
            }
            totalAmountSent += lengthToSend
            totalTimeTaken += duration

            TimeDuration timeDuration = TimeCategory.minus(new Date(now.time + duration), now)
            println " Sent ${GroovyUtils.humanReadableByteCount(totalSent, false)} in ${timeDuration} secs, send ${i + 1}/${totalNrSends}"
        }

        println "Totals:: "
        TimeDuration timeDuration = TimeCategory.minus(new Date(now.time + totalTimeTaken), now)
        println " Sent ${GroovyUtils.humanReadableByteCount(totalAmountSent, false)} in ${timeDuration} secs, avg: ${GroovyUtils.humanReadableByteCount((totalAmountSent/timeDuration.seconds) as int, false)}/sec"
    }

    public void transmit(String s) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()))
        writer.write(s)
        writer.newLine()
        writer.flush()
    }

}

class RunOpts {

    boolean startAsServer = false  // start as client by default

    int dataBlockSize = readableMemorySizeToByteSize "100k"  // blank no max, as per JVM patterns

    String hostName = "localhost"
    String port = "9991"
    String fileToExchange = ""
    int numberOfTimesToSend = 1
    int sizePerSend = readableMemorySizeToByteSize "1k"

    public RunOpts(args) {
        collectRunOpts(args)
    }

    /**
     * eg 2m is 2 megabytes
     * @param memSize
     * @return
     */
    static long readableMemorySizeToByteSize(String memSize) {
        try {
            Integer.parseInt(memSize)
        } catch (all) {
            Map memPow = [k: 1, m: 2, g: 3]
            def memTok = memSize =~ (/^(.*)([kmgt])$/)

            String number = memTok[0][1]
            String memId = memTok[0][2]

            new BigDecimal(number).multiply(new BigDecimal("1024").pow(memPow."${memId}"))
        }
    }


    void collectRunOpts(args) {
        def cli = new CliBuilder(width: 200, stopAtNonOption: false,
                usage: "./NetSpeedCheck.groovy",
                footer: """
            Examples:

            Start the server: ./NetSpeedCheck.groovy  -server
            Connect as client: ./NetSpeedCheck.groovy -host localhost -n 3 -s 1024

            Hint: To set a proxy use something like: -Dhttps.proxyHost=myproxy.com -Dhttps.proxyPort=8000, see: https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html
            Hint: Getting out of memory?  Increase by passing options like -Xmx768m or -Xmx2g and/or reduce the number of threads with -th
            """)
        //log.debug args
        cli.with {
            h(longOpt: 'help', 'usage information', required: false)
            host(longOpt: 'host', "server name to connect to (default= ${hostName})", required: false, args: 1)
            p(longOpt: 'port', "port on the server, (default= ${port})", required: false, args: 1)

            // Optionals
            // Ints
            n(longOpt: 'nrTimes', "Number of times to run send the data, (default= ${numberOfTimesToSend})", required: false, args: 1)
            s(longOpt: 'dataSize', "Amount of random data to send, (default= ${dataBlockSize})", required: false, args: 1)
            chunk(longOpt: 'sizePerSend', "Amount of random data to send, (default= ${sizePerSend})", required: false, args: 1)

            // Booleans
            //nike(longOpt: 'nike', "Just do it. (default=${nikeIt})", required: false)
            server(longOpt: 'startAsServer', "Start as server. (default=${startAsServer})", required: false)

            // Strings
            file(longOpt: 'file', "file to exchange", required: false, args: 1)
        }

        OptionAccessor opt = cli.parse(args)
        if (!opt || opt.h) {
            cli.usage()
            return
        }
        if (opt.host) {
            hostName = opt.host
        }
        if (opt.p) {
            port = opt.p
        }
        if (opt.n) {
            numberOfTimesToSend = Integer.parseInt opt.n
        }
        if (opt.s) {
            dataBlockSize = readableMemorySizeToByteSize opt.s
        }
        if (opt.chunk) {
            sizePerSend = readableMemorySizeToByteSize opt.chunk
        }

        /*if (opt.max) {
            maxFileSize = readableMemorySizeToByteSize opt.max
        }*/

        if (opt.server) {
            startAsServer = true
        }

    }



    @Override
    public java.lang.String toString() {
        return "RunOpts{" +
                "startAsServer=" + startAsServer +
                ", dataBlockSize=" + dataBlockSize +
                ", hostName='" + hostName + '\'' +
                ", port='" + port + '\'' +
                ", fileToExchange='" + fileToExchange + '\'' +
                ", numberOfTimesToSend=" + numberOfTimesToSend +
                '}';
    }
}
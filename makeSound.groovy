import javax.sound.sampled.*

byte[] wavform( int freq, int seconds, int sampleRate ) {
    byte[] ret = new byte[ seconds * sampleRate ]
    ret.length.times { idx ->
        ret[ idx ] = (byte)( Math.sin( ( 2.0 * Math.PI * idx ) / ( sampleRate / freq ) ) * 127 )
    }
    ret
}

int sampleRate = 8000
new AudioFormat( sampleRate, 16, 1, true, true ).with { af ->
    AudioSystem.getSourceDataLine( af ).with { line ->
        line.open( af )
        line.start()
        wavform( 200, 1, sampleRate ).with { byte[] wav ->
            line.write( wav, 0, wav.length )
        }
        line.drain()
        line.close()
    }
}
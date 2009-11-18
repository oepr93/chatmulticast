/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package desktopapplication1;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elbunuelo
 */
public class Conversacion {

    private Socket elSocket;
    public Conversacion(Socket elSocket) {
        this.elSocket = elSocket;
    }
    


    public int Enviar(String Mensaje)
    {
        OutputStream osStream = null;
        try {
            
            osStream = elSocket.getOutputStream();
            OutputStreamWriter oswWriter = new OutputStreamWriter(osStream);
            oswWriter.write("msg=" + Mensaje);
            oswWriter.flush();
            return 0;
        } catch (IOException ex) {
            Logger.getLogger(Conversacion.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        } finally {
            
        }
    }
    public int Recibir(String Mensaje)
    {
        InputStream isStream = null;
        try {
            isStream = elSocket.getInputStream();
            InputStreamReader isrReader = new InputStreamReader(isStream);
            char[] buffer = new char[1000];
            isrReader.read(buffer);
            Mensaje = buffer.toString();
            if(Mensaje.startsWith("msg="))
            {
                Mensaje = Mensaje.substring(4);
                return 0;
            }
            return 1;
        } catch (IOException ex) {
            Logger.getLogger(Conversacion.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        } finally {
            try {
                isStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Conversacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

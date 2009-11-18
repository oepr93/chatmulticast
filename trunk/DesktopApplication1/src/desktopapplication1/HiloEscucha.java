/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package desktopapplication1;

import java.net.Socket;

/**
 *
 * @author elbunuelo
 */
public class HiloEscucha implements Runnable{

    Conversacion convLaConversacion;
    Pestana laPestana;
    public HiloEscucha(Pestana laPestana,Conversacion convLaConversacion) {
        this.convLaConversacion = convLaConversacion;
        this.laPestana = laPestana;
    }

 

    public void run() {
        while(true){
            String sElMensaje="";
            int iResultado = convLaConversacion.Recibir(sElMensaje);
            if(iResultado == 0)
            {
                laPestana.MostrarMensaje(sElMensaje);
            }
            else
            {
                laPestana.MostrarMensaje("Hubo un error recibiendo un Mensaje");
                convLaConversacion.Enviar("El Mensaje no pudo ser enviado por favor intente otra vez");
            }
        }
        
    }

}

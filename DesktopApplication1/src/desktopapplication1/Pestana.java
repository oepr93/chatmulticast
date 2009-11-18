package desktopapplication1;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author elbunuelo
 */
import java.net.Socket;
import javax.swing.*;
public class Pestana extends JPanel{

    private javax.swing.JButton jbEnviar;
    private javax.swing.JScrollPane jscpEscribir;
    private javax.swing.JScrollPane jscpMensajes;
    private javax.swing.JTextPane jtpEscribir;
    private javax.swing.JTextPane jtpMensajes;
    private Conversacion laConversacion;
    private HiloEscucha elHilo;
    private String Usuario;
    
    public Pestana(Socket elSocket,String Usuario)
    {
        laConversacion = new Conversacion(elSocket);
        this.Usuario = Usuario;
        initComponents();
    }

    private void initComponents() {
        /* Instanciando los componentes */
        
        jscpMensajes = new javax.swing.JScrollPane();
        jtpMensajes = new javax.swing.JTextPane();
        jscpEscribir = new javax.swing.JScrollPane();
        jtpEscribir = new javax.swing.JTextPane();
        jbEnviar = new javax.swing.JButton();
        elHilo = new HiloEscucha(this,laConversacion);
        jscpMensajes.setName("jscpMensajes"); // NOI18N

        jtpMensajes.setName("jtpMensajes"); // NOI18N
        jscpMensajes.setViewportView(jtpMensajes);

        jscpEscribir.setName("jscpEscribir"); // NOI18N

        jtpEscribir.setName("jtpEnviar"); // NOI18N
        jscpEscribir.setViewportView(jtpEscribir);

        jbEnviar.setLabel("Enviar");
        jbEnviar.setName("jbEnviar"); // NOI18N

        jbEnviar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbEnviarMouseClicked(evt);
            }
        });
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jscpMensajes, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jscpEscribir, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jbEnviar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jscpMensajes, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jscpEscribir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jbEnviar)
                        .addGap(24, 24, 24)))
                .addContainerGap())
        );
        Thread miHilo = new Thread(elHilo);
        miHilo.start();
    }

     private void jbEnviarMouseClicked(java.awt.event.MouseEvent evt) {
            laConversacion.Enviar(this.jtpEscribir.getText());
            jtpEscribir.setText("");
        }
     public void MostrarMensaje(String ElMensaje)
     {
         jtpMensajes.setText(jtpMensajes.getText() + "\n" + Usuario + "Dice: " +  ElMensaje);
     }
     
}

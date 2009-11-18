package desktopapplication1;

import java.util.Iterator;
import java.util.Vector;
import java.net.*;
import java.io.*;

import javax.swing.JOptionPane;




//ESCUCHA LOS USUARIOS CONECTADOS Y LOS MENSAJES
public class Chat extends Thread implements Runnable{

	private Vector<Usuario> usuariosConectados = new Vector<Usuario>();
	boolean continuar=true;
	String usuario;
	String miIP;
        VentanaConversacionesView formulario;
	public Chat(String nombreUsuario, VentanaConversacionesView formulario){
                this.formulario = formulario;
		try {
			miIP = InetAddress.getLocalHost().getHostAddress();
			start();//ESCUCHA POR MULTICAST
			(new EscucharTCP(this)).start();//ESCUCHA TCP
			usuario = nombreUsuario;
			//MANDAR MULTICAST CON MI NOMBRE
			Thread.sleep(200);
			enviarMensajeMulticast(usuario);
		} catch (Exception e) {System.out.println("error de conexion");}
	}
	

	private void enviarMensajeMulticast(String usuario) throws Exception {
		String mensaje = usuario+"&"+miIP;
		InetAddress ia = null;
		int port = 0;
		ia = InetAddress.getByName("224.0.0.1");
		port = 4446;
		byte[] data = mensaje.getBytes( );
		DatagramPacket dp = new DatagramPacket(data, data.length, ia, port);
		MulticastSocket ms = new MulticastSocket( );
		ms.joinGroup(ia);
		ms.send(dp);
		ms.leaveGroup(ia);
		ms.close( );
	}

	/**
	 * Demonio que escucha udp y replica con mi usuario
	 */
	public void run(){
		while(continuar){
			String ipOrigen="";
			String mensaje = escucharUDP();
			String[] array = mensaje.split("&");
			String usuarioConectado=array[0];
			ipOrigen=array[1];
			if(!yaEstabaConectado(usuarioConectado)){
				reportarUsuarioConectado(usuarioConectado, ipOrigen);
				try {
					System.out.println("Recibo multicast de usuario "+usuarioConectado+" con ip= "+ipOrigen);
					enviarRespuesta(usuario, ipOrigen);
				} catch (Exception e) {
					System.out.println("error de conexion");
				}
			}
		}
	}

	private void enviarRespuesta(String usuario, String ipOrigen) throws Exception{
		Socket socket = new Socket(ipOrigen, 4446);
		OutputStream os=null;
		DataOutputStream  dos=null;
		os = socket.getOutputStream();
		dos=new DataOutputStream(os);
		dos.writeUTF("user="+usuario+"&"+ipOrigen);
	}

	private boolean yaEstabaConectado(String usuarioConectado) {
		Iterator<Usuario> i = getUsuariosConectados().iterator();
		while(i.hasNext()){
			Usuario u = i.next();
			if(u.getNombre().equalsIgnoreCase(usuarioConectado))return true;
		}
		return false;
	}

	private String escucharUDP() {
		InetAddress group = null;
		String result="";
		int port = 0;
		try {
			group = InetAddress.getByName("224.0.0.1");
			port = 4446;
		}
		catch (Exception ex) {
			System.out.println("error de conexion");
			System.exit(1);
		}
		MulticastSocket ms = null;
		try {
			ms = new MulticastSocket(port);
			ms.joinGroup(group);
			byte[] buffer = new byte[8192];
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
			ms.receive(dp);
			String s = (new String(dp.getData( ))).trim();
			System.out.println(s);
			result=s.trim();
		}
		catch (IOException ex) {
			System.out.println("error de conexion");
			System.exit(1);
		}
		finally {
			if (ms != null) {
				try {
					ms.leaveGroup(group);
					ms.close( );
				}
				catch (IOException ex) {}
			}
		}
		return result;
	}

	public void reportarUsuarioConectado(String user, String ip) {
		if(user.equals(usuario)){
			JOptionPane.showMessageDialog(null, "Se supone que la conexion fue exitosa por que recibiste tu propio multicast", "CONEXION EXITOSA", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(null, "USUARIO CONECTADO: "+user, "USUARIO CONECTADO!!!!", JOptionPane.INFORMATION_MESSAGE);
		Usuario u = new Usuario();
		u.setIp(ip);
		u.setNombre(user);
		getUsuariosConectados().add(u);
                formulario.agregarUsuario(user);

	}

	public void iniciarNuevaConversacion(String user, Socket socket, String Mensaje) {
		System.out.println("voy a iniciar una conversacion con: "+user);
                formulario.recibirConversacion(socket, usuario, Mensaje);
		try {
			enviar("mensajerecibido por "+usuario, socket);
		} catch (Exception e) {
			System.out.println("error de conexion");
			System.exit(1);
		}
	}

	public void enviar(String texto, Socket socket) throws Exception{
		OutputStream os=null;
		DataOutputStream  dos=null;
		os = socket.getOutputStream();
		dos=new DataOutputStream(os);
		dos.writeUTF(texto);
	}

    /**
     * @return the usuariosConectados
     */
    public Vector<Usuario> getUsuariosConectados() {
        return usuariosConectados;
    }
}
class EscucharTCP extends Thread implements Runnable{

	Chat c;
	
	public EscucharTCP(Chat c){
		this.c=c;
	}
	
	public void run(){
		ServerSocket sk=null;
		try {
			sk = new ServerSocket(4446);
		} catch (IOException e1) {
			System.out.println("error de conexion");
			System.exit(1);
		}
		while(true){
			try {

				Socket socket = sk.accept();
				String mensaje = recibir(socket);
				System.out.println("recibo mensaje"+mensaje);
				if(mensaje.startsWith("user="))
                                {
                                    //c.reportarUsuarioConectado(mensaje.substring(5), socket.getInetAddress().getHostAddress());
                                    String[] msg = mensaje.substring(5).split("&");
                                    c.reportarUsuarioConectado(msg[0], msg[1]);

				}
				else if(mensaje.startsWith("msg="))
                                {
                                        String user = mensaje.substring(4);
                                        String primermensaje = recibir(socket);
					c.iniciarNuevaConversacion(user, socket,primermensaje);
				}
			} catch (Exception e) {
				System.out.println("error de conexion");
				System.exit(1);
			}
		}
	}

	private String recibir(Socket socket) throws Exception{
		InputStream is=null;
		is = socket.getInputStream();
		DataInputStream dis=new DataInputStream(is);
		String txt=dis.readUTF();
		return txt;
	}
}	
package formularios;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataListener;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.io.Console;
/**
 *
 * @author cesar.ramirez
 */
public class huella extends javax.swing.JApplet {

    /**
     * Initializes the applet huella
     */
    @Override
    public void init() {      
        
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(huella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(huella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(huella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(huella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the applet */
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
       
    }
    
    // Variables para capturar, enrolar y verificar
    
    private DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture();
    private DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    private DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification();
    private DPFPTemplate template ;
    public static String TEMPLATE_PROPERTY = "template";
    public DPFPFeatureSet featuresInscripcion;
    public DPFPFeatureSet featuresVerificacion; 
    boolean conHuella = false ;
    
    
    
    
    // Funcion que inicia todos los eventos del lector
    
    protected void IniciarDerecha ()  {                   
        //Metodo para saber si el Sensor esta Activado o Conectado  
        if(Lector.isStarted() == false){
            start();
        }
        
        EnviarTexto("Inicia captura indice Derecho");
        Lector.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            @Override
            public void readerConnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        EnviarTexto ("Sensor de huella esta activado o conectado");
                        }
                });
                
            }            
        //Metodo para saber si el Sensor esta Desactivado o Desconectado      
            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        EnviarTexto ("Sensor de huella esta desactivdo o desconectado");
                    }
                });
            
            System.runFinalization();}        
        });         
        //Metodo que se ejecuta cuando el dedo es colocado sobre el lector
        Lector.addSensorListener(new DPFPSensorAdapter() {
            @Override
            public void fingerTouched(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(() -> {
                    EnviarTexto("El dedo derecho ha sido colocado sobre el lector de huellas");
                    conHuella = true ;
                });
        }
            
        @Override
        public void fingerGone(final DPFPSensorEvent e) {
            SwingUtilities.invokeLater(() -> {
                EnviarTexto("El dedo derecho ha sido quitado sobre el lector de huellas");

                conHuella = false ;
            });
        }
        });
        
        //Cualquier error que nos da, este metodo lo captura
        Lector.addErrorListener(new DPFPErrorAdapter () {
            public void errorReader(final DPFPErrorEvent e) {
                SwingUtilities.invokeLater(new Runnable () {
                    public void run() {
                        EnviarTexto( "Error: " + e.getError());
                    }
                });
            }
        });
        //Metodo para saber si la huella ha sido Capturada
        Lector.addDataListener(new DPFPDataAdapter() { 
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run () {
                        EnviarTexto("Huella Derecha capturada");
                        ProcesarCapturaDer(e.getSample());
                        stop();
                    }
                });                
            }            
        }); 
        
        System.runFinalization();
        
    }      
    
    
    protected void IniciarIzquierda ()  {                   
        //Metodo para saber si el Sensor esta Activado o Conectado           
        if(Lector.isStarted() == false){
            start();
        }
        
        EnviarTexto("Inicia captura indice Izquierdo");
        
        Lector.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            @Override
            public void readerConnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        EnviarTexto ("Sensor de huella esta activado o conectado");
                        }
                });
            }            
        //Metodo para saber si el Sensor esta Desactivado o Desconectado      
            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        EnviarTexto ("Sensor de huella esta desactivdo o desconectado");
                    }
                });
            }        
        });         
        //Metodo que se ejecuta cuando el dedo es colocado sobre el lector
        Lector.addSensorListener(new DPFPSensorAdapter() {
            @Override
            public void fingerTouched(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(() -> {
                    EnviarTexto("El dedo izquierdo ha sido colocado sobre el lector de huellas");
                    conHuella = true ;
                });
        }
            
        @Override
        public void fingerGone(final DPFPSensorEvent e) {
            SwingUtilities.invokeLater(() -> {
                EnviarTexto("El dedo izquierdo ha sido quitado sobre el lector de huellas");

                conHuella = false ;
            });
        }
        });
        
        //Cualquier error que nos da, este metodo lo captura
        Lector.addErrorListener(new DPFPErrorAdapter () {
            public void errorReader(final DPFPErrorEvent e) {
                SwingUtilities.invokeLater(new Runnable () {
                    public void run() {
                        EnviarTexto( "Error: " + e.getError());
                    }
                });
            }
        });
        //Metodo para saber si la huella ha sido Capturada
        Lector.addDataListener(new DPFPDataAdapter() { 
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run () {
                        EnviarTexto("Huella Izquierda capturada");
                        ProcesarCapturaIzq(e.getSample());
                        stop();
                    }
                });                
            }            
        }); 
       
        System.runFinalization();
    }    
    //funcion que obtiene las cracteristicas de la huella
    public DPFPFeatureSet extraerCaracteristicas(DPFPSample sample, DPFPDataPurpose purpose) {
        
        DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        try {
            //nos retorna las caracteristicas extraidas de la huella
            return extractor.createFeatureSet(sample, purpose);
        }catch (DPFPImageQualityException e){
            return null;
        }        
    }
    
    
    
    public void ProcesarCapturaDer(DPFPSample sample){

    if (conHuella == true ){        
        // System.out.println("Las caracteristicas de la huella han sido creadas");
        Image imageDerecha = CrearImagenHuellaD(sample);
          // Dibuja la huella dactilar capturada
        DibujarHuellaDer(imageDerecha);
        conHuella = false; 
              
        }   
    }
    
  
    
    
    public void ProcesarCapturaIzq(DPFPSample sample){

    if (conHuella == true ){        
        // System.out.println("Las caracteristicas de la huella han sido creadas");
        Image imageIzquierda = CrearImagenHuellaI(sample);
         // Dibuja la huella dactilar capturada
        DibujarHuellaIzq(imageIzquierda);
        conHuella = false; 
              
        }   
    }
    
    public Image CrearImagenHuellaD(DPFPSample sample){
        
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
        
    }
    
    public Image CrearImagenHuellaI(DPFPSample sample){

    return DPFPGlobal.getSampleConversionFactory().createImage(sample);

    }
    
    
    ///*** capturar imagen de la huella 
    
    
    public void DibujarHuellaDer (Image imageDerecha) {
        
        lblImagenHuella.setIcon(new ImageIcon(
                        imageDerecha.getScaledInstance(lblImagenHuella.getWidth(), lblImagenHuella.getHeight(), Image.SCALE_DEFAULT)));
        repaint(); 
        // mirar icon desde el label
        ImageIcon icon = (ImageIcon) lblImagenHuella.getIcon();
        //copiar imagen
        BufferedImage huella = new BufferedImage(icon.getIconWidth(),icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = huella.createGraphics();
        g2.drawImage(icon.getImage(), 0, 0, icon.getImageObserver());
        g2.dispose();       
        
        //Ruta donde guardara la imagen        
        // File file = new File ("C:\\Users\\cesar.ramirez\\Documents\\pruebasArchivos\\huella.jpg");
      
        File file = new File ("C:\\CiaSci\\indice derecho.jpg");     
        
        //********************************
        try {
	                
                ImageIO.write( huella , "jpg", file );
	
        } catch (IOException e) {
	
            System.out.println("Error de escritura");
		}
        
    }   
    
    public void DibujarHuellaIzq (Image imageIzquierda) {
        
        lblImagenHuella1.setIcon(new ImageIcon(
                        imageIzquierda.getScaledInstance(lblImagenHuella1.getWidth(), lblImagenHuella1.getHeight(), Image.SCALE_DEFAULT)));
        repaint(); 
        // mirar icon desde el label
        ImageIcon icon = (ImageIcon) lblImagenHuella1.getIcon();
        //copiar imagen
        BufferedImage huella = new BufferedImage(icon.getIconWidth(),icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = huella.createGraphics();
        g2.drawImage(icon.getImage(), 0, 0, icon.getImageObserver());
        g2.dispose();       
        
        //Ruta donde guardara la imagen        
        // File file = new File ("C:\\Users\\cesar.ramirez\\Documents\\pruebasArchivos\\huella.jpg");
      
        File file = new File ("C:\\CiaSci\\indice izquierdo.jpg");     
        
        //********************************
        try {
	                
                ImageIO.write( huella , "jpg", file );
	
        } catch (IOException e) {
	
            System.out.println("Error de escritura");
		}
        
    }
      
    //******************
    
    public void EnviarTexto(String string){
        txtSalida.append(string + "\n");
       
    }
    
    public void start() {
        
         Lector.startCapture();
         EnviarTexto("Utilizando el lector de huellas dactilar");
    }
    
    public void stop() {
        
         Lector.stopCapture();
         EnviarTexto("No se esta utilizando el lector de huellas dactilar");
    }
    
    public DPFPTemplate getTemplate() {
        
        return template;
        
    }
    
    public void setTemplate(DPFPTemplate template) {
        
        DPFPTemplate old = this.template;
        this.template = template;
        firePropertyChange(TEMPLATE_PROPERTY, old, template);
        
    }
    	public void salir()
	{
		System.exit(0);
	}
    
    /**
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
   // @SuppressWarnings("unchecked");
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pnlHuella = new javax.swing.JPanel();
        lblImagenHuella = new javax.swing.JLabel();
        lblImagenHuella1 = new javax.swing.JLabel();
        pnlSalida = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtSalida = new javax.swing.JTextArea();
        pnlAcciones = new javax.swing.JPanel();
        btnDedoDerecho = new javax.swing.JButton();
        btnDedoIzquierdo = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        pnlHuella.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Captura de huellas", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        javax.swing.GroupLayout pnlHuellaLayout = new javax.swing.GroupLayout(pnlHuella);
        pnlHuella.setLayout(pnlHuellaLayout);
        pnlHuellaLayout.setHorizontalGroup(
            pnlHuellaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHuellaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblImagenHuella, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(lblImagenHuella1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlHuellaLayout.setVerticalGroup(
            pnlHuellaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHuellaLayout.createSequentialGroup()
                .addGroup(pnlHuellaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImagenHuella, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(lblImagenHuella1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        txtSalida.setColumns(20);
        txtSalida.setForeground(new java.awt.Color(240, 240, 240));
        txtSalida.setRows(5);
        txtSalida.setUI(null);
        txtSalida.setAlignmentX(0.0F);
        txtSalida.setAlignmentY(0.0F);
        txtSalida.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jScrollPane1.setViewportView(txtSalida);

        pnlAcciones.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Acciones", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        btnDedoDerecho.setText("Indice derecho");
        btnDedoDerecho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDedoDerechoActionPerformed(evt);
            }
        });

        btnDedoIzquierdo.setText("Indice Izquierdo");
        btnDedoIzquierdo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDedoIzquierdoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAccionesLayout = new javax.swing.GroupLayout(pnlAcciones);
        pnlAcciones.setLayout(pnlAccionesLayout);
        pnlAccionesLayout.setHorizontalGroup(
            pnlAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccionesLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(btnDedoDerecho, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(btnDedoIzquierdo, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        pnlAccionesLayout.setVerticalGroup(
            pnlAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccionesLayout.createSequentialGroup()
                .addGroup(pnlAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDedoDerecho)
                    .addComponent(btnDedoIzquierdo))
                .addGap(0, 10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlSalidaLayout = new javax.swing.GroupLayout(pnlSalida);
        pnlSalida.setLayout(pnlSalidaLayout);
        pnlSalidaLayout.setHorizontalGroup(
            pnlSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlAcciones, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        pnlSalidaLayout.setVerticalGroup(
            pnlSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSalidaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlSalida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlHuella, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlHuella, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnDedoDerechoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDedoDerechoActionPerformed
        txtSalida.setText("");
        IniciarDerecha();  
        System.runFinalization();
    }//GEN-LAST:event_btnDedoDerechoActionPerformed

    private void btnDedoIzquierdoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDedoIzquierdoActionPerformed
        txtSalida.setText(""); 
        IniciarIzquierda();         
         System.runFinalization();
    }//GEN-LAST:event_btnDedoIzquierdoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDedoDerecho;
    private javax.swing.JButton btnDedoIzquierdo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblImagenHuella;
    private javax.swing.JLabel lblImagenHuella1;
    private javax.swing.JPanel pnlAcciones;
    private javax.swing.JPanel pnlHuella;
    private javax.swing.JPanel pnlSalida;
    private javax.swing.JTextArea txtSalida;
    // End of variables declaration//GEN-END:variables
}

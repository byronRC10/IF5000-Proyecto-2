package GUI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

/**
 *
 * @author byron
 */
public class VistaPDF {
    
    public VistaPDF(String ruta){
      String filePath = ruta;

      // construir un controlador
      SwingController controller = new SwingController();

     // Construir un SwingViewFactory configurado con el controlador
     SwingViewBuilder factory = new SwingViewBuilder(controller);

     JPanel viewerComponentPanel = factory.buildViewerPanel();

     ComponentKeyBinding.install(controller, viewerComponentPanel);

     controller.getDocumentViewController().setAnnotationCallback(
      new org.icepdf.ri.common.MyAnnotationCallback(
             controller.getDocumentViewController()));

     JFrame window = new JFrame("Using the Viewer Component");
     window.getContentPane().add(viewerComponentPanel);
     window.pack();
     window.setVisible(true);

    // abre la vista
     controller.openDocument(filePath);
    }//constructor
    
}// fin

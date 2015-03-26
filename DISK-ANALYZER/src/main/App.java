package main;
 
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
 
public class App extends Application 
{     
       
    public static void main(String[] args)
    {
        launch(args);
    }
     
    @Override
    public void start(Stage arg0) throws Exception 
    {
        arg0.setTitle("Java Disk Analyzer");
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MAIN2.fxml"));
        Scene scene= new Scene(root,780,540);
        arg0.getIcons().add(new Image("/fxml/images/2387_2c9e299d_512x512_256x256(1).png"));
        arg0.setMaximized(true);;
        arg0.setScene(scene);
        
        arg0.show();
    }
}
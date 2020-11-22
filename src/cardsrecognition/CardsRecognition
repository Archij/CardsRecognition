package cardsrecognition;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Артур Михайлович Степченко
 */
public class CardsRecognition {

    public static void main(String[] args) {
        
      RecognitionFunctions RF;
      ImageFilter imageFilter = new ImageFilter();
      File dir = new File(args[0]); //путь к папке с изображениями 
      for(final File imgFile : dir.listFiles()) { //идем через все файлы в папке
          if(imageFilter.accept(imgFile)){ //если файл - это png изображение
              BufferedImage img = null;
              try {
                   img = ImageIO.read(imgFile); //читаем изображение
              } catch (IOException e) {
                  System.out.println("Изображение " + imgFile.getName().replaceFirst("[.][^.]+$", "") + " невозможно открыть!");
          
              }
              String results = "";
              RF = new RecognitionFunctions(img);
              for (int i=0; i<5; i++) { //идем через возможных 5 карт
                  results = results + RF.getRankOrSuit(i, 'r'); //получаем значимость
                  results = results + RF.getRankOrSuit(i, 's'); //получаем масть
              }
              System.out.println(imgFile.getName().replaceFirst("[.][^.]+$", "") + " - " + results); //выводим название изображения и карты в центре
          }
      }
    }
    
}

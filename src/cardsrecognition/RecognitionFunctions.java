package cardsrecognition;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 *
 * @author Артур Михайлович Степченко
 */
public class RecognitionFunctions {
    
    private BufferedImage img;
    private final int topLeftCornerX[] = {149,219,289,364,439}; //Индексы колонок верхнего левого угла фрагмента матрицы изображения
    private final int topLeftCornerY[] = {589, 617}; //Индексы рядов верхнего левого угла фрагмента матрицы изображения
    private final int rankSize[] = {41, 31}; //размер фрагмента изображения значимости карты
    private final int suitSize[] = {23, 21}; //размер фрагмента изображения масти карты

    
    public RecognitionFunctions(BufferedImage img)
    {
        this.img = img;
    }
    
    public String getRankOrSuit(int number, char type) //функция, которая возвращает значение значимости
    {
        String ranksuit = "";
        boolean isCard = false;
        BufferedImage img1 = null;
        if (type == 'r') {
            img1 = img.getSubimage(topLeftCornerX[number], topLeftCornerY[0], rankSize[0], rankSize[1]); // взятие области значимости в картинке
        } else if (type == 's') {
            img1 = img.getSubimage(topLeftCornerX[number], topLeftCornerY[1], suitSize[0], suitSize[1]); // взятие области масти в картинке
        }
        boolean isGray = false;
                          
                        
        if (img1.getRGB(img1.getWidth()-1, img1.getHeight()-1)==-8882056) { //если последний пиксель - это серый пиксель (значение -8882056)
           isGray = true; //то это изображение с серым покрытием
           img1 = getBinaryImage(img1,'s'); //надо конвертировать в бинарное изображение
        }
        
        String originalString =  getImageAsString(img1); //преобразование значимости/масти данной карты в бинарную строку
        
        if (number > 2) { //если это место четвертой или пятой карты
            Color c = new Color(img1.getRGB(img1.getWidth()-1, img1.getHeight()-1)); //получаем значение пикселя (R - красная полоса, G - зеленая полоса, B - синяя полоса)
            isCard = isFourthorFifthCard(c.getRed(), c.getGreen(), c.getRed()); //есть ли четвертая или пятая карта
        }

        if (number < 3 || (number > 2 && isCard == true)) { //если это одна из первых трех карт или есть тоже четвертая или пятая карта
            File dir = null;
            if (type == 'r') {
                dir = new File("images\\ranks"); //13 значений
            } else if (type == 's') {
                dir = new File("images\\suits"); //4 масти
            }
            
            int min = 1000000;
            for(final File imgFile : dir.listFiles()) {
                BufferedImage target = null;
                try {
                     target = ImageIO.read(imgFile);
                     if (isGray == true) {
                        target = getBinaryImage(target, 't');
                     }
                     
                } catch (IOException e) {
                     System.out.println("Картинку " + imgFile.getName().replaceFirst("[.][^.]+$", "") + " невозможно открыть!");
                }
                String targetString =  getImageAsString(target); //значимость как бинарная строка
                
                int levenshtein = levenshtein(originalString, targetString, 1000); //расстояние Левенштейна
                
                if (levenshtein < min) { //поиск минимума расстояния Левенштейна
                    min = levenshtein;
                    ranksuit = imgFile.getName().replaceFirst("[.][^.]+$", "");
                }
            }
        }
        
        return ranksuit;
    }
    
    public boolean isFourthorFifthCard(int r, int g, int b) { //Есть ли четвертая или пятая карта
        if ((r == 255 && g == 255 && b == 255) ||  (r == 120 && g == 120 && b == 120)) { //если это белый или серый пиксель
            return true; //то есть карта
        }
        else {
            return false;
        }
    }
    
    public int levenshtein(String source, String target, int threshold) { //функция расстояния Левенштейна
 
        int N1 = source.length(); //длина строки символов изображения значимости или масти
        int N2 = target.length(); //длина строки символо изображения-шаблона

        int p[] = new int[N1 + 1]; //'предыдущий' массив
        int d[] = new int[N1 + 1]; //'текущий' массив
        int temp[]; //для помощи в замене p и d

        final int boundary = Math.min(N1, threshold) + 1; //заполняем значения начальной таблицы
        for (int i = 0; i < boundary; i++) {
            p[i] = i;
        }

        Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE); //значение над самой правой записью будет проигнорировано в следующих итерациях цикла
        Arrays.fill(d, Integer.MAX_VALUE);

        for (int j = 1; j <= N2; j++) { //проходит через t
            char t_j = target.charAt(j - 1); //j-й символ t
            d[0] = j;

            int min = Math.max(1, j - threshold); //вычислить индексы, ограничить размер массива
            //int max = (j > Integer.MAX_VALUE - threshold) ? N1 : Math.min(N1, j + threshold);
            int max;
            if (j > Integer.MAX_VALUE - threshold) {
                max = N1;
            } else {
                max = Math.min(N1, j + threshold);
            }

            if (min > max) {
                return -1;
            }

            if (min > 1) { //игнорировать запись слева от крайнего левого угла
                d[min - 1] = Integer.MAX_VALUE;
            }

            for (int i = min; i <= max; i++) { //выполняет итерацию [min, max] в строке символов "source"
                if (source.charAt(i - 1) == t_j) {
                    d[i] = p[i - 1]; //по диагонали влево и вверх
                } else {
                    d[i] = 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]); //1 + минимум от ячейки слева, вверх, по диагонали влево и вверх
                }
            }

            // копировать текущие значения расстояний
            temp = p;
            p = d;
            d = temp;
        }

        if (p[N1] <= threshold) { //если p[n] больше порога, нет гарантии, что это правильное расстояние
            return p[N1];
        }
        return -1;
    }

    public String getImageAsString(BufferedImage symbol) { //преобразование изображения в бинарную строку
        short whiteBg = -1;
        StringBuilder binaryString = new StringBuilder();  
        for (short y = 1; y < symbol.getHeight(); y++) {
             for (short x = 1; x < symbol.getWidth(); x++) {
                 int rgb = symbol.getRGB(x, y);
                 binaryString.append(rgb == whiteBg ? " " : "*");
             }
        }
        return binaryString.toString();
    
    }
    
    public BufferedImage getBinaryImage(BufferedImage img, char type) { //конвертация изображения в бинарное
        BufferedImage new_image = img;
        for (int i = 0; i<new_image.getWidth(); i++) {
            for (int j = 0; j<new_image.getHeight(); j++) {
                if (type == 's') { //если это изображение значимости или масти карты
                    if (new_image.getRGB(i, j)==-8882056) { //если это серый пиксель
                        new_image.setRGB(i, j, -1); //сохранение как белого пикселя
                    } else {
                        new_image.setRGB(i, j, 0); //все остальные пиксели соранить как черные
                    }
                }
                else if (type == 't') { //если это изображение-шаблон
                    if (new_image.getRGB(i, j)!=-1) {
                       new_image.setRGB(i, j, 0); //если пиксель не белый, сохранить как черный
                    }
                }
                    
            }
        }
        return new_image;        
    }
    
}

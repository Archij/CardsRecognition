package cardsrecognition;

import java.io.File;

/**
 *
 * @author Артур Михайлович Степченко
 */

class ImageFilter{ //проверяет файл в папке, которую указал пользователь
    private final String PNG = "png";

    public boolean accept(File file) {
        if(file != null) {
            if(file.isDirectory())
                return false;
            String extension = getExtension(file);
            if(extension != null && isSupported(extension))
                return true;
        }
        return false;
    }
    private String getExtension(File file) {
        if(file != null) {
            String filename = file.getName();
            int dot = filename.lastIndexOf('.');
            if(dot > 0 && dot < filename.length()-1)
                return filename.substring(dot+1).toLowerCase();
        }
        return null;
    }
    private boolean isSupported(String ext) { //файл должен быть с расширением .png
        return ext.equalsIgnoreCase(PNG);
    }
}

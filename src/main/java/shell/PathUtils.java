package shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class PathUtils {

    public static String getHomePath(){
        return System.getenv("HOME");
    }

    private static final List<Path> pathDirs = new ArrayList<>();

    public static String getCurrDir() {
        return System.getProperty("user.dir");
    }

    public static List<Path> getPathDirs(){
        return pathDirs;
    }

    static void setPathDirs(){
        Path pathObj;
        for(String dir : System.getenv("PATH").split(":")){
            pathObj = Paths.get(dir);
            if(Files.exists(pathObj)){
                pathDirs.add(pathObj);
            }
        }
    }
}
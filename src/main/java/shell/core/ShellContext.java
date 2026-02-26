package shell.core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ShellContext {

    private Path cwd;
    private Path home;
    private List<Path> pathDirs = new ArrayList<>();

    public ShellContext(){
        setCWD(Paths.get(System.getProperty("user.dir")));
        setHome(Paths.get(System.getProperty("user.home")));
        setPathDirs(System.getenv("PATH"));
    }

    public Path getCWD(){
        return cwd;
    }

    public void setCWD(Path newCWD){
        this.cwd = newCWD;
    }

    public Path getHome(){
        return home;
    }

    public void setHome(Path newHome){
        this.home = newHome;
    }

    public List<Path> getPathDirs() {
        return pathDirs;
    }

    public void setPathDirs(String newPathStr){
        Path pathObj;
        this.pathDirs.clear();
        for(String dir : newPathStr.split(File.pathSeparator)){

            this.pathDirs.add(Paths.get(dir));
            pathObj = Paths.get(dir);
            if(Files.exists(pathObj)){
                this.pathDirs.add(pathObj);
            }
        }
    }
}

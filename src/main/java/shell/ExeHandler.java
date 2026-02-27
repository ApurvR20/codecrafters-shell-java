package shell;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ExeHandler {

    public Path findExec(List<Path> pathDirs, String fileName){
        Path filePath;
        for(Path p : pathDirs){
            filePath = p.resolve(fileName);
            if(Files.exists(filePath) && Files.isExecutable(filePath)){
                return filePath;
            }
        }

        return null;
    }

    public void runExe(String[] arguments, StringBuilder out, Path cwd) {
        try {
            ProcessBuilder pb = new ProcessBuilder(arguments);
            pb.directory(cwd.toFile());
            Process p = pb.start();

            Thread t1 = new Thread(() -> {
                try (BufferedReader br =
                             new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        out.append(line).append('\n');
                    }
                } catch (Exception ignored) {}
            });

            Thread t2 = new Thread(() -> {
                try (BufferedReader br =
                             new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception ignored) {}
            });

            t1.start();
            t2.start();

            p.waitFor();

            t1.join();
            t2.join();

            if(!out.isEmpty() && out.charAt(out.length()-1) == '\n') {
                out.deleteCharAt(out.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

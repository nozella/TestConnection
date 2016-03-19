package br.com.nozella.testconnection.main;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Marcos on 19/03/2016.
 */
public class App implements Runnable {

    private static final Logger log = Logger.getLogger(App.class);
    private static final Logger report = Logger.getLogger("reportAppender");

    private static final String pathname = String.format("%s/Desktop/ipportlist.txt", System.getProperty("user.home"));
    private static File defaultFile = new File(pathname);
    private static File fileToRead = defaultFile;

    public static void main(String[] args) {
        boolean canRun = validateFile(args);
        if (canRun) {
            new App().run();
        }
        return;
    }

    private static boolean validateFile(String[] args) {
        if (args.length > 0 && args[0] != null) {
            File file = new File(args[0]);
            if (file.exists() && !file.isDirectory()) {
                fileToRead = file;
            } else {
                log.warn(
                        String.format(
                                "Arquivo %s nao encontrado. Tentando ler arquivo %s",
                                args[0],
                                fileToRead.getAbsoluteFile()
                        )
                );
            }
        }
        if (!fileToRead.canRead()) {
            log.error(String.format("Arquivo inascessivel, favor informar local para o arquivo ou deixar no local padrao: %s", defaultFile.getAbsoluteFile()));
            return false;
        }
        return true;
    }

    public void run() {
        log.info("init");

        String actualTry = null;
        List<String> readLines = null;
        try {
            readLines = FileUtils.readLines(fileToRead);
        } catch (IOException e) {
            log.error("erro ao ler arquivo", e);
        }
        report.info(String.format("Execucao: %s", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())));
        for (String str: readLines) {
            try {
                actualTry = str;
                String[] strSplited = str.split(" ");
                SocketAddress socketAddress = new InetSocketAddress(strSplited[0],Integer.valueOf(strSplited[1]));
                new Socket().connect(socketAddress, 1000);
                report.info(String.format("%s: OK", actualTry));
            } catch (SocketTimeoutException e) {
                log.error("Erro ao conectar", e);
                report.info(String.format("%s: Timeout", actualTry));
            } catch (IOException e) {
                log.error("Erro durante a execucao", e);
            }
        }
        log.info("end");
    }
}

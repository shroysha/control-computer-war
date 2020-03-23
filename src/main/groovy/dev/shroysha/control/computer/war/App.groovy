package dev.shroysha.control.computer.war

import dev.shroysha.control.computer.ejb.ControlComputerEntityScanTag
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.swing.*
import java.awt.*
import java.util.Queue

@SpringBootApplication
@RestController
class App {

    static void main(String[] args) {
        SpringApplication.run(App.class, args)
    }

    public static final boolean BROWSING_SUPPORTED = Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
    public static final Queue<String> COMMAND_QUEUE = new LinkedList<>()

    @RequestMapping(ControlComputerEntityScanTag.QUEUE_ADD)
    void queueAdd(String command) {
        COMMAND_QUEUE.add(command)
    }

    @RequestMapping(ControlComputerEntityScanTag.QUEUE_EXECUTE)
    void queueExecute(String command) throws IOException {
        while (!COMMAND_QUEUE.isEmpty()) {
            sysCmd(COMMAND_QUEUE.poll())
        }
    }

    @RequestMapping(ControlComputerEntityScanTag.WEB_OPEN)
    void webOpen(String path) throws Exception {
        if (!BROWSING_SUPPORTED) {
            throw new UnsupportedOperationException("Browsing not supported")
        }
        path = path.replaceFirst("openpage ", "")
        URL url
        try {
            url = new URL(path)
        } catch (MalformedURLException ex) {
            url = new URL("http://" + path)
        }
        Desktop.getDesktop().browse(url.toURI())
    }

    @RequestMapping(ControlComputerEntityScanTag.POPUP_TEXT)
    void popupText(String text) {
        JOptionPane.showConfirmDialog(null, text)
    }

    @RequestMapping(ControlComputerEntityScanTag.POPUP_ANSWER)
    String popupAnswer(String text) {
        return JOptionPane.showInputDialog(text)
    }

    @RequestMapping(ControlComputerEntityScanTag.SYS_CMD)
    void sysCmd(String command) throws IOException {
        Runtime.getRuntime().exec(command)
    }

    @RequestMapping(ControlComputerEntityScanTag.SYS_RESTART)
    void sysRestart() throws IOException {
        sysCmd("shutdown -r -t 0")
    }


    @RequestMapping(ControlComputerEntityScanTag.SYS_TASKKILL)
    void sysTaskkill(String task) throws IOException {
        sysCmd("taskkill /F /IM " + task)
    }

    @RequestMapping(ControlComputerEntityScanTag.SERVER_QUIT)
    void serverQuit() {
        System.exit(42)
    }

}
